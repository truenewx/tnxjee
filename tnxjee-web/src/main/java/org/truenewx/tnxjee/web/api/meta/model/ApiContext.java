package org.truenewx.tnxjee.web.api.meta.model;

import java.util.HashMap;
import java.util.Map;

/**
 * API上下文环境
 */
public class ApiContext extends ApiMetaProperties {

    private Map<String, String> headers = new HashMap<>();

    public Map<String, String> getHeaders() {
        return this.headers;
    }

}
