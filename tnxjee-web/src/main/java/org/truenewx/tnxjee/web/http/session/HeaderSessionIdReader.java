package org.truenewx.tnxjee.web.http.session;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * 从Header读取的sessionId读取器
 */
public class HeaderSessionIdReader {

    private String headerName;

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return this.headerName;
    }

    public List<String> readSessionIds(HttpServletRequest request) {
        List<String> values = new ArrayList<>();
        if (this.headerName != null) {
            Enumeration<String> headers = request.getHeaders(this.headerName);
            while (headers.hasMoreElements()) {
                String value = headers.nextElement();
                if (StringUtils.isNotBlank(value)) {
                    values.add(value);
                }
            }
        }
        return values;
    }

}
