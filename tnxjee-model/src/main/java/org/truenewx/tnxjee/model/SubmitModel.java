package org.truenewx.tnxjee.model;

import org.truenewx.tnxjee.model.entity.Entity;

/**
 * 提交模型，用于视图层向逻辑层提交数据
 *
 * @author jianglei
 */
public interface SubmitModel<T extends Entity> extends TransportModel {

}
