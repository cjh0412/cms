package com.cjhdev.cms.user.config.filter;

import com.cjhdev.cms.user.service.CustomerService;
import config.JwtAuthenticationProvider;
import domain.common.UserVo;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@WebFilter(urlPatterns = "/customer/*")
@RequiredArgsConstructor
public class CustomerFilter implements Filter {
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final CustomerService customerService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("X-AUTH-TOKEN");
        if(!jwtAuthenticationProvider.validateToken(token)) {
            throw new ServletException("Invalid token");
        }
        UserVo vo = jwtAuthenticationProvider.getUserVo(token);
        customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
                () -> new ServletException("Invalid access")
        );

        filterChain.doFilter(request, response);


    }

}
