package org.truenewx.tnxjee.web.http.session;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SessionId解决切面
 */
@Aspect
@Component
public class SessionIdResolveAspect {

    @Autowired
    private HeaderSessionIdReader headerSessionIdReader;

    @SuppressWarnings("unchecked")
    @Around("execution(* org.springframework.session.web.http.DefaultCookieSerializer.readCookieValues(..)))")
    public Object aroundReadCookieValues(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = (HttpServletRequest) point.getArgs()[0];
        List<String> values = this.headerSessionIdReader.readHeaderValues(request);
        values.addAll((List<String>) point.proceed());
        return values;
    }

}
