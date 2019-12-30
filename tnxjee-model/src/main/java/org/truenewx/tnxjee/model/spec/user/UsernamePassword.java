package org.truenewx.tnxjee.model.spec.user;

/**
 * 用户名密码
 *
 * @author jianglei
 */
public class UsernamePassword {

    private String username;
    private String password;

    public UsernamePassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

}
