package com.hcservice.service.impl;

import com.hcservice.dao.PermissionMapper;
import com.hcservice.domain.model.Permission;
import com.hcservice.domain.model.Role;
import com.hcservice.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 权限资源
 * FilterInvocationSecurityMetadataSource的默认实现是
 * DefaultFilterInvocationSecurityMetadataSource
 */
@Service
public class UserInvocationSecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource {

    @Autowired
    PermissionMapper permissionMapper;
    /* key 是url+method ,value 是对应url资源的角色列表 */
    private Map<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<>();


    /**
     *  注意：
     *  @PostConstruct 用于在依赖关系注入完成之后需要执行的方法，以执行任何初始化。
     *  此方法必须在将类放入服务之前调用，且只执行一次。
     */
    @PostConstruct
    public void init(){
        List<Permission> permissions = permissionMapper.findAll();
        permissions.forEach(item->{
            List<Role> roles = item.getRoles();
            List<ConfigAttribute> configAttributes = new ArrayList<>();
            for (Role r : roles) {
                configAttributes.add(new SecurityConfig(r.getRoleName()));
            }
            requestMap.put(new AntPathRequestMatcher(item.getUrl()),configAttributes);
        });
        System.out.println(requestMap.toString());
    }

    /**
     * getAttributes方法返回本次访问需要的权限，可以有多个权限。
     * 在上面的实现中如果没有匹配的url直接返回null，
     * 也就是没有配置权限的url默认都为白名单，想要换成默认是黑名单只要修改这里即可。
     *
     * 访问配置属性（ConfigAttribute）用于给定安全对象（通过的验证）
     *
     * @param o 安全的对象
     * @return 用于传入的安全对象的属性。 如果没有适用的属性，则应返回空集合。
     * @throws IllegalArgumentException 如果传递的对象不是SecurityDatasource实现支持的类型，则抛出异常
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        if(requestMap.isEmpty()){
            init();
        }
        final HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            if (entry.getKey().matches(request)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     *
     *
     * getAllConfigAttributes方法如果返回了所有定义的权限资源，
     * Spring Security会在启动时校验每个ConfigAttribute是否配置正确，不需要校验直接返回null。
     *
     *
     * 如果可用，则返回由实现类定义的所有ConfigAttribute。
     *
     * AbstractSecurityInterceptor使用它对针对它ConfigAttribute的每个配置属性执行启动时验证。
     *
     * @return ConfigAttribute，如果没有适用的，就返回null
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
