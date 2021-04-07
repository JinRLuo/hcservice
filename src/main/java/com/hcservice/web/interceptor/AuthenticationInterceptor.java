package com.hcservice.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.hcservice.annotation.PassToken;
import com.hcservice.annotation.UserLoginToken;
import com.hcservice.common.ErrorCode;
import com.hcservice.domain.model.User;
import com.hcservice.common.BaseResult;
import com.hcservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    // 定义一个线程域，存放登录用户
    private static final ThreadLocal<User> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //进入方法之前进行的操作
        //获取token
        String token  =  request.getHeader("token");
        //如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if(method.isAnnotationPresent(PassToken.class))
        {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if(passToken.required()){
                return true;
            }
        }
        Integer userId = null;
        if(method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if(userLoginToken.required()){
                if(token == null){
                    responseError(response);
                    return false;
                }
                //获取token的userid
                try{
                    userId = Integer.valueOf(JWT.decode(token).getAudience().get(0));
                }
                catch (JWTDecodeException e){
                    responseError(response);
                    return false;
                }
                User user = userService.getUserByUserId(userId);
                if(user==null){
                    responseError(response);
                    return false;
                }
                //验证token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
                try{
                    jwtVerifier.verify(token);
                }catch (JWTVerificationException e){
                    responseError(response);
                    return false;
                }
                threadLocal.set(user);
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private void responseError(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(JSON.toJSONString(BaseResult.create(ErrorCode.ACCOUNT_NOT_LOGIN, "fail")));
        out.flush();
        out.close();
    }

    public static User getLoginUser() {
        return threadLocal.get();
    }
}
