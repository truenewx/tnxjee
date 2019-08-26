package org.truenewx.tnxjee.repo.validation.rule;

/**
 * 集合大小规则
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SizeRule extends RangeRule<Integer> {

    public SizeRule(int min, int max) {
        super(min, max);
    }

    public SizeRule() {
        this(0, Integer.MAX_VALUE);
    }

}
