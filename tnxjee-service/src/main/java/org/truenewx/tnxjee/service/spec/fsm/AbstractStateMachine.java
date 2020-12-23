package org.truenewx.tnxjee.service.spec.fsm;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.truenewx.tnxjee.model.entity.unity.Unity;
import org.truenewx.tnxjee.model.spec.user.UserIdentity;
import org.truenewx.tnxjee.service.transaction.annotation.WriteTransactional;

/**
 * 抽象的有限状态机
 *
 * @param <U> 实体类型
 * @param <K> 标识类型
 * @param <S> 状态枚举类型
 * @param <T> 转换枚举类型
 * @param <I> 用户标识类型
 * @author jianglei
 */
public abstract class AbstractStateMachine<U extends Unity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, I extends UserIdentity<?>>
        implements StateMachine<U, K, S, T, I> {
    /**
     * 起始状态
     */
    private S startState;

    private List<StateTransitAction<U, K, S, T, I>> actions = new ArrayList<>();

    public AbstractStateMachine(S startState) {
        this.startState = startState;
    }

    public void setActions(Collection<? extends StateTransitAction<U, K, S, T, I>> actions) {
        this.actions.addAll(actions);
    }

    @Override
    public S getStartState() {
        return this.startState;
    }

    @Override
    public Set<T> getTransitions(S state) {
        Set<T> transitions = new HashSet<>();
        for (StateTransitAction<U, K, S, T, I> action : this.actions) {
            if (ArrayUtils.contains(action.getBeginStates(), state)) {
                transitions.add(action.getTransition());
            }
        }
        return transitions;
    }

    @Override
    public S[] getBeginStates(T transition) {
        for (StateTransitAction<U, K, S, T, I> action : this.actions) {
            if (action.getTransition() == transition) {
                return action.getBeginStates();
            }
        }
        return null;
    }

    private StateTransitAction<U, K, S, T, I> getStateTransitAction(S state, T transition, Object condition) {
        for (StateTransitAction<U, K, S, T, I> action : this.actions) {
            if (action.getTransition() == transition
                    && action.getEndState(state, condition) != null) {
                return action;
            }
        }
        return null;
    }

    @Override
    public S getNextState(S state, T transition, Object condition) {
        StateTransitAction<U, K, S, T, I> action = getStateTransitAction(state, transition, condition);
        if (action != null) {
            return action.getEndState(state, condition);
        }
        return null;
    }

    @Override
    @WriteTransactional
    public U transit(I userIdentity, K key, T transition, Object context) {
        U entity = loadUnity(userIdentity, key, context);
        S state = getState(entity);
        Object condition = getCondition(userIdentity, entity, context);
        StateTransitAction<U, K, S, T, I> action = getStateTransitAction(state, transition, condition);
        if (action == null) {
            throw new StateIntransitableException(state, transition);
        }
        if (!action.execute(userIdentity, entity, context)) {
            return null;
        }
        return entity;
    }

    /**
     * 加载指定实体，需确保返回非空的实体，如果找不到指定实体，则需抛出业务异常
     *
     * @param userIdentity 用户标识
     * @param key          实体标识
     * @param context      上下文
     * @return 实体
     */
    protected abstract U loadUnity(I userIdentity, K key, Object context);

    /**
     * 从指定实体中获取状态值。实体可能包含多个状态属性，故不通过让实体实现获取状态的接口来实现
     *
     * @param entity 实体
     * @return 状态值
     */
    protected abstract S getState(U entity);

    /**
     * 获取转换条件，用于定位转换动作
     *
     * @param userIdentity 用户标识
     * @param entity       实体
     * @param context      转换上下文
     * @return 转换条件
     */
    protected abstract Object getCondition(I userIdentity, U entity, Object context);

}
