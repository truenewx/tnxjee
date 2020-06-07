package org.truenewx.tnxjee.web.view.menu.model;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.tnxjee.core.util.CollectionUtil;

/**
 * 菜单链接
 */
public class MenuLink extends MenuItem {

    private static final long serialVersionUID = 5574091037889165424L;
    private String path;
    private String target;
    private String rank;
    private String permission;
    private List<MenuOperation> operations = new ArrayList<>();

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

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

    public List<MenuOperation> getOperations() {
        return this.operations;
    }

    public void setOperations(List<MenuOperation> operations) {
        CollectionUtil.reset(operations, this.operations);
    }

    public MenuLink cloneWithoutOperations() {
        MenuLink link = new MenuLink();
        link.path = this.path;
        link.target = this.target;
        link.rank = this.rank;
        link.permission = this.permission;
        clone(this, link);
        return link;
    }

    @Override
    public MenuLink clone() {
        MenuLink link = cloneWithoutOperations();
        this.operations.forEach(operation -> {
            link.operations.add(operation.clone());
        });
        return link;
    }

}
