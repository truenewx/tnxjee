package org.truenewx.tnxjee.core.util.encrypt;

/**
 * 带密钥的加密器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface KeyEncrypter {

    String encrypt(Object source, Object key);

}
