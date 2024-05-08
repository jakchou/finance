package com.defaulttenant.hotswap.web.interceptor;

import com.defaulttenant.hotswap.domain.http.HttpResponse;
import com.defaulttenant.hotswap.util.GlobalContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import java.util.Objects;

import java.util.List;

/**
 * @author sys
 */
@ControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 判断是否需要拦截，这里可以根据返回值类型或HttpMessageConverter类型来判断
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        List<HttpResponse> list = GlobalContext.findHttpResponseFieldsInSessionVariables();
        Object returnBody = body;
        if(!CollectionUtils.isEmpty(list)){
            for (int l = 0; l < list.size(); l++) {
                HttpResponse httpResponse =list.get(l);
                httpResponse.copyProperties(httpResponse,response);
                if(Objects.nonNull(httpResponse.body)){
                   returnBody = httpResponse.body;
                }
            }
        }
        return returnBody;
    }
}