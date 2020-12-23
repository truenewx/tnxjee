package org.truenewx.tnxjee.service.spec.fsm;

import java.io.Serializable;

import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;

/**
 * 状态转换动作
 *
 * @param <U> 单体类型
 * @param <K> 标识类型
 * @param <S> 状态枚举类型
 * @param <T> 转换枚举类型
 * @param <I> 用户标识类型
 * @author jianglei
 */
public interface StateTransitAction<U extends Unity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, I extends UserIdentity<?>> {
    /**
     * 获取转换枚举。每个转换动作都对应且仅对应一个转换枚举
     *
     * @return 转换枚举
     */
    T getTransition();

    /**
     * @return 当前转换动作可能的起始状态集
     */
    S[] getBeginStates();

    /**
     * 获取在指定起始状态执行当前转换动作后的结束状态
     *
     * @param beginState 起始状态
     * @param condition  条件
     * @return 结束状态，如果在指定起始状态下不能根据指定条件执行当前转换动作，则返回null
     */
    S getEndState(S beginState, Object condition);

    /**
     * 指定用户对指定标识表示的单体，在指定上下文情况时，执行动作
     *
     * @param userIdentity 用户标识
     * @param entity       单体标识
     * @param context      上下文
     * @return 动作是否正常执行
     */
    boolean execute(I userIdentity, U entity, Object context);

}
