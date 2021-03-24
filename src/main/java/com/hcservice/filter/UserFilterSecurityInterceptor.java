package com.hcservice.filter;

import com.hcservice.service.impl.UserAccessDecisionManager;
import com.hcservice.service.impl.UserInvocationSecurityMetadataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import java.io.IOException;
import java.util.logging.LogRecord;

@Component
public class UserFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    private final UserInvocationSecurityMetadataSourceService userInvocationSecurityMetadataSourceService;
    private final UserAccessDecisionManager userAccessDecisionManager;

    @Autowired
    public UserFilterSecurityInterceptor(UserInvocationSecurityMetadataSourceService userInvocationSecurityMetadataSourceService, UserAccessDecisionManager userAccessDecisionManager) {
        this.userInvocationSecurityMetadataSourceService = userInvocationSecurityMetadataSourceService;
        this.userAccessDecisionManager = userAccessDecisionManager;
    }

    @PostConstruct
    public void init() {
        super.setAccessDecisionManager(userAccessDecisionManager);
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.userInvocationSecurityMetadataSourceService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        FilterInvocation filterInvocation = new FilterInvocation(servletRequest,servletResponse,filterChain);
        invoke(filterInvocation);
    }

    private void invoke(FilterInvocation filterInvocation) throws IOException, ServletException {
        // 调用父类的beforeInvocation ==> accessDecisionManager.decide(..)
        InterceptorStatusToken token = super.beforeInvocation(filterInvocation);
        try {
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        } finally {
            super.finallyInvocation(token);
        }
        super.afterInvocation(token, null);
    }
}
