package org.truenewx.tnxjee.service.impl.unity;

import java.io.Serializable;

import org.springframework.util.Assert;
import org.truenewx.tnxjee.model.core.SubmitModel;
import org.truenewx.tnxjee.model.core.unity.OwnedUnity;
import org.truenewx.tnxjee.repo.OwnedUnityRepo;
import org.truenewx.tnxjee.service.api.unity.ModelOwnedUnityService;
import org.truenewx.tnxjee.service.api.unity.SimpleOwnedUnityService;

/**
 * 抽象的从属单体的服务
 *
 * @author jianglei
 * @param <T> 单体类型
 * @param <K> 标识类型
 * @param <O> 所属者类型
 */
public abstract class AbstractOwnedUnityService<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends AbstractUnityService<T, K>
        implements SimpleOwnedUnityService<T, K, O>, ModelOwnedUnityService<T, K, O> {

    @Override
    protected OwnedUnityRepo<T, K, O> getRepo() {
        return getRepo(getEntityClass());
    }

    @Override
    public T find(O owner, K id) {
        return getRepo().findByOwnerAndId(owner, id);
    }

    @Override
    public T load(O owner, K id) {
        T unity = find(owner, id);
        assertNotNull(unity);
        return unity;
    }

    @Override
    public T add(O owner, T unity) {
        if (owner == null) {
            return null;
        }
        unity = beforeSave(owner, null, unity);
        if (unity != null) {
            Assert.isTrue(owner.equals(unity.getOwner()), "owner must equal unity's owner");
            getRepo().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    /**
     * 注意：子类不应修改单体的所属者
     */
    @Override
    public T update(O owner, K id, T unity) {
        if (owner == null || id == null) {
            return null;
        }
        unity = beforeSave(owner, id, unity);
        if (unity != null) {
            Assert.isTrue(owner.equals(unity.getOwner()) && id.equals(unity.getId()),
                    "owner must equal unity's owner, and id must equal unity's id");
            getRepo().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    /**
     * 在保存添加/修改有所属者的单体前调用，负责验证改动的单体数据，并写入返回的结果单体中<br/>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果单体的标识等于传入的指定标识参数
     *
     * @param owner 所属者
     * @param id    要修改的单体标识，为null时表示是添加动作
     * @param unity 存放添加/修改数据的单体对象
     * @return 已写入数据，即将保存的单体
     */
    protected T beforeSave(O owner, K id, T unity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T add(O owner, SubmitModel<T> submitModel) {
        if (owner == null) {
            return null;
        }
        T unity = beforeSave(owner, null, submitModel);
        if (unity != null) {
            Assert.isTrue(owner.equals(unity.getOwner()), "owner must equal unity's owner");
            getRepo().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    @Override
    public T update(O owner, K id, SubmitModel<T> submitModel) {
        if (owner == null || id == null) {
            return null;
        }
        T unity = beforeSave(owner, id, submitModel);
        if (unity != null) {
            Assert.isTrue(owner.equals(unity.getOwner()) && id.equals(unity.getId()),
                    "owner must equal unity's owner, and id must equal unity's id");
            getRepo().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    /**
     * 在保存添加/修改有所属者的单体前调用，负责验证改动的模型数据，并写入返回的结果单体中<br/>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果单体的标识等于传入的指定标识参数
     *
     * @param owner       所属者
     * @param id          要修改的单体标识，为null时表示是添加动作
     * @param submitModel 存放添加/修改数据的提交模型
     * @return 已写入数据，即将保存的从属单体
     */
    protected T beforeSave(O owner, K id, SubmitModel<T> submitModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(O owner, K id) {
        if (owner != null && id != null) {
            T unity = beforeDelete(owner, id);
            if (unity == null) {
                unity = find(owner, id);
            }
            getRepo().delete(unity);
        }
    }

    /**
     * 根据标识删除从属单体前调用，由子类覆写<br/>
     * 子类不覆写或调用父类的本方法，将无法删除单体
     *
     * @param owner 所属者
     * @param id    要删除的单体的标识
     * @return 要删除的单体，可返回null，返回非null值有助于提高性能
     */
    protected T beforeDelete(O owner, K id) {
        throw new UnsupportedOperationException();
    }

}
