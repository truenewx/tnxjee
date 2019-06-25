package org.truenewx.tnxjee.core.util.encrypt;

import org.truenewx.tnxjee.core.util.EncryptUtil;

/**
 * BASE64可逆算法加密器
 *
 * @author jianglei
 */
public class Base64Encrypter implements BidirectionalEncrypter {

    public final static Base64Encrypter INSTANCE = new Base64Encrypter();

    private Base64Encrypter() {
    }

    @Override
    public String encrypt(Object source) {
        return EncryptUtil.encryptByBase64(source);
    }

    @Override
    public String decrypt(String encryptedText) {
        return EncryptUtil.decryptByBase64(encryptedText);
    }
}
