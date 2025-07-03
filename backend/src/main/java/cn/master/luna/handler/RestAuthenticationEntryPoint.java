package cn.master.luna.handler;

import cn.master.luna.util.JacksonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Created by 11's papa on 2025/7/3
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String message = authException.getMessage();
        // todo 根据具体的错误处理不同的情况， 如token过期、格式不正确
        if (authException instanceof InvalidBearerTokenException) {
            message="Invalid Bearer Token";
        }
        ResultHandler error = ResultHandler.error(HttpServletResponse.SC_UNAUTHORIZED,
                message,
                authException.getCause().getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(JacksonUtils.toJSONString(error));
    }
}
