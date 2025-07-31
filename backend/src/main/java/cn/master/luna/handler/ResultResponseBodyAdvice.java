package cn.master.luna.handler;

import cn.master.luna.handler.annotation.NoResultHolder;
import cn.master.luna.util.JacksonUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author Created by 11's papa on 2025/7/2
 */
@RestControllerAdvice
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType) || StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object o, @NonNull MethodParameter methodParameter,
                                  @NonNull MediaType mediaType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> converterType,
                                  @NonNull ServerHttpRequest serverHttpRequest,
                                  @NonNull ServerHttpResponse serverHttpResponse) {
        // 处理空值
        if (o == null && StringHttpMessageConverter.class.isAssignableFrom(converterType)) {
            serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JacksonUtils.toJSONString(ResultHandler.success(o));
        }
        // 直接返回swagger请求结果
        if ("/v3/api-docs/swagger-config".equals(serverHttpRequest.getURI().getPath())) {
            return o;
        }
        if (methodParameter.hasMethodAnnotation(NoResultHolder.class)) {
            return o;
        }
        if (!(o instanceof ResultHandler)) {
            if (o instanceof String) {
                serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return JacksonUtils.toJSONString(ResultHandler.success(o));
            }
            return ResultHandler.success(o);
        }
        return o;
    }
}
