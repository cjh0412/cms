package com.cjhdev.cms.user.config.filter;

import com.cjhdev.cms.user.service.customer.CustomerService;
import config.JwtAuthenticationProvider;
import domain.common.UserVo;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

// customer로 시작하는 모든 경로
@WebFilter(urlPatterns = "/customer/*")
@RequiredArgsConstructor
public class CustomerFilter implements Filter {
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final CustomerService customerService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("X-AUTH-TOKEN");
        // token 값 체크
        if(!jwtAuthenticationProvider.validateToken(token)) {
            throw new ServletException("Invalid token");
        }
        
        // decoding 된 id, email 값과 db 회원 정보 비교
        UserVo vo = jwtAuthenticationProvider.getUserVo(token);
        customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
                () -> new ServletException("Invalid access")
        );

        filterChain.doFilter(request, response);


    }

}
