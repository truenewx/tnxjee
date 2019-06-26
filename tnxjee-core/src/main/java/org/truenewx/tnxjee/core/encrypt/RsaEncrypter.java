package org.truenewx.tnxjee.core.encrypt;

import java.io.InputStream;

import org.truenewx.tnxjee.core.util.EncryptUtil;

/**
 * RSA加密器
 *
 * @author jianglei
 *
 */
public class RsaEncrypter implements KeyBidirectionalEncrypter {

    @Override
    public String encrypt(Object source, Object key) {
        return EncryptUtil.encryptByRsa(source, (InputStream) key);
    }

    @Override
    public String decrypt(String encryptedText, Object key) {
        return EncryptUtil.decryptByRsa(encryptedText, (InputStream) key);
    }

}
