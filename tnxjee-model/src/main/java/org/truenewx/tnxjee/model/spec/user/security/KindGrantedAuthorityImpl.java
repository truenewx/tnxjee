package org.truenewx.tnxjee.model.spec.user.security;

/**
 * 已取得权限的实现
 */
// 定义setter方法的目的是为了便于JSON反序列化
public class KindGrantedAuthorityImpl implements KindGrantedAuthority {

    private static final long serialVersionUID = 7686101572750400289L;

    private GrantedAuthorityKind kind;
    private String name;

    public static KindGrantedAuthorityImpl ofType(String name) {
        KindGrantedAuthorityImpl authority = new KindGrantedAuthorityImpl();
        authority.setKind(GrantedAuthorityKind.TYPE);
        authority.setName(name);
        return authority;
    }

    public static KindGrantedAuthorityImpl ofRank(String name) {
        KindGrantedAuthorityImpl authority = new KindGrantedAuthorityImpl();
        authority.setKind(GrantedAuthorityKind.RANK);
        authority.setName(name);
        return authority;
    }

    public static KindGrantedAuthorityImpl ofPermission(String name) {
        KindGrantedAuthorityImpl authority = new KindGrantedAuthorityImpl();
        authority.setKind(GrantedAuthorityKind.PERMISSION);
        authority.setName(name);
        return authority;
    }

    @Override
    public GrantedAuthorityKind getKind() {
        return this.kind;
    }

    public void setKind(GrantedAuthorityKind kind) {
        this.kind = kind;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getAuthority();
    }

}
