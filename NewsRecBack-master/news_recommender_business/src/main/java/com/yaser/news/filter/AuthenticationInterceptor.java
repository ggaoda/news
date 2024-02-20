package com.yaser.news.filter;

import com.yaser.news.constant.Roles;
import com.yaser.news.controller.globalHandler.APIException;
import com.yaser.news.constant.ResultCode;
import com.yaser.news.dataEntity.RecUser;
import com.yaser.news.service.UserService;
import com.yaser.news.utils.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) {
        if (object instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) object;
            Method method = handlerMethod.getMethod();
            Class<?> declaringClass = method.getDeclaringClass();
            if (method.isAnnotationPresent(Admin.class) || declaringClass.isAnnotationPresent(UseToken.class)) {
                //如果有admin注解，则必须进行认证
                return authorizeAdminToken(httpServletRequest);
            } else {
                UseToken useToken = null;
                if (method.isAnnotationPresent(UseToken.class)) {
                    useToken = method.getAnnotation(UseToken.class);
                } else if (declaringClass.isAnnotationPresent(UseToken.class)) {
                    useToken = declaringClass.getAnnotation(UseToken.class);
                }
                //判断当前method或class是否被useToken注解了
                if (useToken != null) {
                    return authorizeToken(httpServletRequest, useToken);
                }
            }
        }
        return true;
    }

    private boolean authorizeAdminToken(HttpServletRequest request) {
        try {
            long userId = JWTUtils.ParseToken(request);//解析请求中的token信息
            log.info("userId:" + userId);
            if (userId == 0) {
                throw new APIException(ResultCode.TOKEN_NOT_FOUND_UID);
            }
            if (ServiceContextHolder.getContext() == null) {
                //如果上下文不存在用户信息，则加载进去
                RecUser recUser = this.userService.loadUserByUserId(userId);//通过ID加载用户信息
                if (recUser.getRole() != Roles.ADMIN) {
                    throw new APIException(ResultCode.PERMISSION_DENY);
                }
                ServiceContext serviceContext = new ServiceContext(recUser);
                ServiceContextHolder.setContext(serviceContext);//将加载的用户信息写入到SecurityContext中
            }
        } catch (ExpiredJwtException e) {
            log.info("------------------------ExpiredJwtException" + e.getMessage());
            throw new APIException(ResultCode.TOKEN_EXPIRED);
        } catch (SignatureException e) {
            log.info("------------------------SignatureException" + e.getMessage());
            throw new APIException(ResultCode.ILLEGAL_TOKEN);
        }
        return true;
    }

    private boolean authorizeToken(HttpServletRequest request, UseToken useToken) {
        // 执行认证
        try {
            long userId = JWTUtils.ParseToken(request);//解析请求中的token信息
            log.info("userId:" + userId);
            if (userId == 0) {
                //判断是不是必须要token
                if (useToken.must()) {
                    throw new APIException(ResultCode.TOKEN_NOT_FOUND_UID);
                } else {
                    return true;
                }
            }
            ServiceContext serviceContext = ServiceContextHolder.getContext();
            if (serviceContext == null || serviceContext.getRecUser().getUid() != userId) {
                //如果上下文不存在用户信息，则加载进去
                RecUser recUser = this.userService.loadUserByUserId(userId);//通过ID加载用户信息
                serviceContext = new ServiceContext(recUser);
                ServiceContextHolder.setContext(serviceContext);//将加载的用户信息写入到SecurityContext中
            }
        } catch (ExpiredJwtException e) {
            log.info("------------------------ExpiredJwtException" + e.getMessage());
            throw new APIException(ResultCode.TOKEN_EXPIRED);
        } catch (SignatureException e) {
            log.info("------------------------SignatureException" + e.getMessage());
            throw new APIException(ResultCode.ILLEGAL_TOKEN);
        } catch (APIException e) {
            if (useToken.must()) {
                log.info("------------------------APIException" + e.getMessage());
                throw new APIException(ResultCode.TOKEN_NOT_FOUND_UID);
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {

    }
}