package org.truenewx.tnxjee.service.api.unity;

import java.io.Serializable;

import org.truenewx.tnxjee.model.core.SubmitModel;
import org.truenewx.tnxjee.model.core.unity.OwnedUnity;

/**
 * 基于传输模型的从属单体服务
 *
 * @author jianglei
 */
public interface ModelOwnedUnityService<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends OwnedUnityService<T, K, O> {
    /**
     * 添加从属单体
     *
     * @param owner       所属者
     * @param submitModel 存放添加数据的提交模型对象
     * @return 添加的单体
     */
    T add(O owner, SubmitModel<T> submitModel);

    /**
     * 修改从属单体<br/>
     * 注意：不应修改单体的所属者
     *
     * @param owner       所属者
     * @param id          要修改单体的标识
     * @param submitModel 存放修改数据的提交模型对象
     * @return 修改后的单体
     *
     */
    T update(O owner, K id, SubmitModel<T> submitModel);
}
