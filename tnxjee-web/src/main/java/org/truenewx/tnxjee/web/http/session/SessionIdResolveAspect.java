package org.truenewx.tnxjee.web.http.session;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SessionId解决切面
 */
@Aspect
@Component
public class SessionIdResolveAspect {

    @Autowired(required = false)
    private HeaderSessionIdReader headerSessionIdReader;
//
//    @SuppressWarnings("unchecked")
//    @Around("execution(* org.springframework.session.web.http.DefaultCookieSerializer.readCookieValues(..)))")
//    public Object aroundReadCookieValues(ProceedingJoinPoint point) throws Throwable {
//        HttpServletRequest request = (HttpServletRequest) point.getArgs()[0];
//        List<String> values = this.headerSessionIdReader.readSessionIds(request);
//        values.addAll((List<String>) point.proceed());
//        return values;
//    }

}
