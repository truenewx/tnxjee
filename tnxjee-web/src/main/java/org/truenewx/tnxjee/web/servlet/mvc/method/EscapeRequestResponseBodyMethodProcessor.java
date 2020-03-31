package org.truenewx.tnxjee.web.servlet.mvc.method;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.truenewx.tnxjee.model.spec.Terminal;
import org.truenewx.tnxjee.model.spec.enums.Program;
import org.truenewx.tnxjee.web.util.WebUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * 转义请求和响应Body的方法处理器
 */
public class EscapeRequestResponseBodyMethodProcessor
        implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {

    private RequestResponseBodyMethodProcessor delegate;

    public EscapeRequestResponseBodyMethodProcessor(RequestResponseBodyMethodProcessor delegate) {
        this.delegate = delegate;
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return this.delegate != null && this.delegate.supportsParameter(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object object = this.delegate.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        BeanPropertyEscapeProcessor.INSTANCE.accept(object, false);
        return object;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return this.delegate != null && this.delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (webRequest instanceof ServletWebRequest) {
            HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
            Terminal terminal = WebUtil.getRequestTerminal(request);
            if (terminal.getProgram() == Program.NATIVE) { // 原生应用才需要反转义
                BeanPropertyEscapeProcessor.INSTANCE.accept(returnValue, true);
            }
        }

        this.delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

}
