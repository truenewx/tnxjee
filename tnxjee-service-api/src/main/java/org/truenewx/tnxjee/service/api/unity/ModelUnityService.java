package org.truenewx.tnxjee.service.api.unity;

import java.io.Serializable;

import org.truenewx.tnxjee.model.core.SubmitModel;
import org.truenewx.tnxjee.model.core.unity.Unity;

/**
 * 基于传输模型的单体服务
 *
 * @author jianglei
 */
public interface ModelUnityService<T extends Unity<K>, K extends Serializable>
        extends UnityService<T, K> {
    /**
     * 添加单体
     *
     * @param submitModel 存放添加数据的提交模型对象
     * @return 添加的单体
     */
    T add(SubmitModel<T> submitModel);

    /**
     * 修改单体
     *
     * @param id          要修改单体的标识
     * @param submitModel 存放修改数据的提交模型对象
     * @return 修改后的单体
     *
     */
    T update(K id, SubmitModel<T> submitModel);

}
