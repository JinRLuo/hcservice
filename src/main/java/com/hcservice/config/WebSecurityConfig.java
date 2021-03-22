package com.hcservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcservice.common.ErrorCode;
import com.hcservice.domain.response.BaseResult;
import com.hcservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.PrintWriter;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/login").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                //anyRequest需要放在最后才不会报错 表示前面拦截剩下的请求
                .anyRequest().authenticated()
                .and()
                .formLogin()
                //登录成功
                .successHandler((req, resp, authentication) -> {
                    Object principal = authentication.getPrincipal();
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(principal));
                    out.flush();
                    out.close();
                })
                .failureHandler((req, resp, e) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    BaseResult result;
                    if (e instanceof AccountExpiredException) {
                        result = BaseResult.create(ErrorCode.ACCOUNT_EXPIRED, "fail");
                    } else if (e instanceof DisabledException) {
                        result = BaseResult.create(ErrorCode.ACCOUNT_DISABLED, "fail");
                    } else if (e instanceof BadCredentialsException) {
                        result = BaseResult.create(ErrorCode.ACCOUNT_LOGIN_FAIL, "fail");
                    } else {
                        result = BaseResult.create(ErrorCode.UNKNOWN_ERROR, "fail");
                    }
                    out.write(new ObjectMapper().writeValueAsString(result));
                    out.flush();
                    out.close();
                })
                .loginProcessingUrl("/doLogin")
                .usernameParameter("account")
                .passwordParameter("password")
                .permitAll()
                .and()
                .rememberMe()
                .key("hcservice")  //用来加密cookie中的token
                .and()
                //注销
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((req, resp, authentication) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    BaseResult result = BaseResult.create("注销成功");
                    out.write(new ObjectMapper().writeValueAsString(result));
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .csrf().disable()
                //未认证处理
                .exceptionHandling()
                .authenticationEntryPoint((req, resp, authException) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    BaseResult result = BaseResult.create(ErrorCode.ACCOUNT_NOT_LOGIN,"fail");
                    out.write(new ObjectMapper().writeValueAsString(result));
                    out.flush();
                    out.close();
                });
    }



    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**","/images/**",
                "/v2/api-docs",
                "/configuration/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    //配置角色的继承关系
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return hierarchy;
    }

}
