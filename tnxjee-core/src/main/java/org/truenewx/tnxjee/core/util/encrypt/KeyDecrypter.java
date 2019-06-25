package org.truenewx.tnxjee.core.util.encrypt;

/**
 * 带密钥的解密器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface KeyDecrypter {

    String decrypt(String encryptedText, Object key);

}
