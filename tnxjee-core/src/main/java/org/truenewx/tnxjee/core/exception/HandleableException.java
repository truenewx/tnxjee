package org.truenewx.tnxjee.core.exception;

/**
 * 可处理的异常<br/>
 * 仅作为标识，在进行异常处理时便于判断
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class HandleableException extends Exception {

    private static final long serialVersionUID = -4552090901512143756L;

    public HandleableException() {
        super();
    }

    public HandleableException(String message) {
        super(message);
    }

}
