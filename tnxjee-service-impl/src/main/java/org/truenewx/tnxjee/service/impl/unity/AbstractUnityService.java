package org.truenewx.tnxjee.service.impl.unity;

import java.io.Serializable;

import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.exception.HandleableException;
import org.truenewx.tnxjee.model.definition.SubmitModel;
import org.truenewx.tnxjee.model.definition.unity.Unity;
import org.truenewx.tnxjee.repo.UnityRepo;
import org.truenewx.tnxjee.service.api.unity.ModelUnityService;
import org.truenewx.tnxjee.service.api.unity.SimpleUnityService;
import org.truenewx.tnxjee.service.impl.AbstractService;

/**
 * 抽象的单体服务
 *
 * @author jianglei
 * @param <T> 单体类型
 * @param <K> 单体标识类型
 */
public abstract class AbstractUnityService<T extends Unity<K>, K extends Serializable>
        extends AbstractService<T> implements SimpleUnityService<T, K>, ModelUnityService<T, K> {

    @Override
    protected UnityRepo<T, K> getRepo() {
        return getRepo(getEntityClass());
    }

    @Override
    public T find(K id) {
        return getRepo().findById(id).orElse(null);
    }

    @Override
    public T load(K id) {
        T unity = find(id);
        assertNotNull(unity);
        return unity;
    }

    @Override
    public T add(T unity) {
        T newUnity = beforeSave(null, unity);
        Assert.isTrue(newUnity != unity, "the returned unity must not be the input unity");
        if (newUnity != null) {
            getRepo().save(newUnity);
            afterSave(newUnity);
        }
        return newUnity;
    }

    @Override
    public T update(K id, T unity) {
        if (id == null) {
            return null;
        }
        unity = beforeSave(id, unity);
        if (unity != null) {
            Assert.isTrue(id.equals(unity.getId()), "id must equal unity's id");
            getRepo().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    /**
     * 在保存添加/修改单体前调用，负责验证改动的单体数据，并写入返回的结果单体中<br/>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果单体的标识等于传入的指定标识参数
     *
     * @param id    要修改的单体标识，为null时表示是添加动作
     * @param unity 存放添加/修改数据的单体对象
     * @return 已写入数据，即将保存的单体
     * @throws HandleableException 如果数据验证失败
     */
    protected T beforeSave(K id, T unity) {
        throw new UnsupportedOperationException();
    }

    /**
     * 单体保存之后调用，负责后续处理
     *
     * @param unity 被保存的单体
     * @throws HandleableException 如果处理过程中出现错误
     */
    protected void afterSave(T unity) {
    }

    @Override
    public T add(SubmitModel<T> submitModel) {
        T unity = beforeSave(null, submitModel);
        if (unity != null) {
            getRepo().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    @Override
    public T update(K id, SubmitModel<T> submitModel) {
        if (id == null) {
            return null;
        }
        T unity = beforeSave(id, submitModel);
        if (unity != null) {
            Assert.isTrue(id.equals(unity.getId()), "id must equal unity's id");
            getRepo().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    /**
     * 在保存添加/修改单体前调用，负责验证改动的模型数据，并写入返回的结果单体中<br/>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果单体的标识等于传入的指定标识参数
     *
     * @param id          要修改的单体标识，为null时表示是添加动作
     * @param submitModel 存放添加/修改数据的单体对象
     * @return 已写入数据，即将保存的单体
     * @throws HandleableException 如果数据验证失败
     */
    protected T beforeSave(K id, SubmitModel<T> submitModel) {
        throw new UnsupportedOperationException();
    }

    /**
     * 删除单体
     *
     * @param id 要删除的单体的标识
     * @throws HandleableException 如果删除校验失败
     */
    @Override
    public void delete(K id) {
        if (id != null) {
            T unity = beforeDelete(id);
            if (unity == null) {
                unity = find(id);
            }
            getRepo().delete(unity);
        }
    }

    /**
     * 根据标识删除单体前调用，由子类覆写<br/>
     * 不覆写或子类调用父类的本方法，将无法删除单体
     *
     * @param id 要删除的单体的标识
     * @return 要删除的单体，可返回null，返回非null值有助于提高性能
     * @throws HandleableException 如果校验不通过
     */
    protected T beforeDelete(K id) {
        throw new UnsupportedOperationException();
    }

}
