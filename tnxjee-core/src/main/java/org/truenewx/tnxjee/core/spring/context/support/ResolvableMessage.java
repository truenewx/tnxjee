package org.truenewx.tnxjee.core.spring.context.support;

import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * 可解析的消息，简化的默认消息资源可解析实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ResolvableMessage extends DefaultMessageSourceResolvable {

    private static final long serialVersionUID = 3490721513523692887L;

    public ResolvableMessage(String code, Object... arguments) {
        super(new String[] { code }, arguments, null);
    }

    public ResolvableMessage(String code, Object[] arguments, String defaultMessage) {
        super(new String[] { code }, arguments, defaultMessage);
    }

}
