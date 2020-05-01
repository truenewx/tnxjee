package org.truenewx.tnxjee.web.view.menu.model;

/**
 * 菜单操作
 */
public class MenuOperation extends MenuItem {

    private String rank;
    private String permission;

    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPermission() {
        return this.permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public MenuOperation clone() {
        MenuOperation operation = new MenuOperation();
        operation.rank = this.rank;
        operation.permission = this.permission;
        clone(this, operation);
        return operation;
    }
}
