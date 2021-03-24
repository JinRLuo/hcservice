package com.hcservice.service.impl;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserAccessDecisionManager implements AccessDecisionManager {

    /**
     * 权限鉴定
     *
     * @param authentication   from SecurityContextHolder.getContext() => userDetails.getAuthorities()
     * @param o                就是FilterInvocation对象，可以得到request等web资源。
     * @param collection       from MetaDataSource.getAttributes()，已经被框架做了非空判断
     * @throws AccessDeniedException   如果由于身份验证不具有所需的权限或ACL特权而拒绝访问
     * @throws InsufficientAuthenticationException 如果由于身份验证没有提供足够的信任级别而拒绝访问
     */
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        if(collection == null){
            return;
        }

        for (ConfigAttribute configAttribute : collection) {
            /* 资源的权限 */
            String attribute = configAttribute.getAttribute();
            /* 用户的权限 */
            for (GrantedAuthority authority : authentication.getAuthorities()) { // 当前用户的权限
                if (attribute.trim().equals(authority.getAuthority().trim())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
