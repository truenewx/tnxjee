package org.truenewx.tnxjee.webmvc.cors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.DefaultCorsProcessor;

/**
 * 忽略空配置的Cors处理器
 *
 * @author jianglei
 */
public class IgnoreNullConfigCorsProcessor extends DefaultCorsProcessor {

    @Override
    public boolean processRequest(CorsConfiguration config, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (config == null) {
            return true;
        }
        return super.processRequest(config, request, response);
    }

}
