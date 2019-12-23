package org.truenewx.tnxjee.core.crypto;

/**
 * 带密钥的加密器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface KeyEncryptor {

    String encrypt(Object source, Object key);

}
