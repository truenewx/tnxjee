package org.truenewx.tnxjee.web.api.meta;

import org.truenewx.tnxjee.model.Model;

import java.util.Locale;

/**
 * API模型元数据解决器
 *
 * @author jianglei
 */
public interface ApiModelMetaResolver {

    ApiModelMeta resolve(Class<? extends Model> modelClass, Locale locale);

}
