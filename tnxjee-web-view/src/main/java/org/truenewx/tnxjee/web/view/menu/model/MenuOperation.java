package org.truenewx.tnxjee.web.view.menu.model;

/**
 * 菜单操作
 */
public class MenuOperation extends MenuItem {

    private String role;
    private String permission;

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPermission() {
        return this.permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

}
