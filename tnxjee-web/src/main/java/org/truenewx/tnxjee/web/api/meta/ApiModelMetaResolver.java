package org.truenewx.tnxjee.web.api.meta;

import org.truenewx.tnxjee.model.Model;

import java.util.Locale;
import java.util.Map;

/**
 * API模型元数据解决器
 *
 * @author jianglei
 */
public interface ApiModelMetaResolver {

    Map<String, ApiModelPropertyMeta> resolve(Class<? extends Model> modelClass, Locale locale);

}
