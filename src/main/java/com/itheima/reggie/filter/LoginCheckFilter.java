package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.itheima.reggie.common.R;


/**
 * 拦截用户所有请求，查看其是否登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        //1.取出当前请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求"+requestURI);

        //2.判断当前请求访问的资源是否需要拦截
        //虽然访问这些静态资源的页面时，不会拦截，但是访问到静态资源页面后，页面需要通过查数据库加载一些资源，从而导致不登录直接访问主页面也会跳转回登录页面
        String[] urlPatterns = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //3.如果不需要拦截，则直接放行
        boolean check = check(urlPatterns, requestURI);
        if (check){
            filterChain.doFilter(request,response);
            return;
        }

        //4.1 判断当前请求的员工是否登录，如果已经登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            //将登录用户的id存到LocalThread
            Long employeeId  = (Long)request.getSession().getAttribute("employee");
            BaseContext.set(employeeId);
            filterChain.doFilter(request,response);
            return;
        }
        //4.2 判断当前请求的用户是否登录，如果已经登录，则直接放行
        if(request.getSession().getAttribute("user")!=null){
            //将登录用户的id存到LocalThread
            Long userId  = (Long)request.getSession().getAttribute("user");
            BaseContext.set(userId);
            filterChain.doFilter(request,response);
            return;
        }

        //5.如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配
     * @param urlPattern
     * @param requestURI
     * @return
     */
    public boolean check(String[] urlPattern,String requestURI){
        for (String item : urlPattern) {
            boolean matchResult = PATH_MATCHER.match(item, requestURI);
            if (matchResult){
                return true;
            }
        }
        return false;
    }

}
