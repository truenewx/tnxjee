package org.truenewx.tnxjee.webmvc.view.menu.model;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.tnxjee.core.util.CollectionUtil;

/**
 * 菜单
 */
public class Menu extends MenuElement {

    private static final long serialVersionUID = -1355233532947830688L;

    private String userType;
    private List<MenuItem> items = new ArrayList<>();

    public Menu(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return this.userType;
    }

    public List<MenuItem> getItems() {
        return this.items;
    }

    public void setItems(List<MenuItem> items) {
        CollectionUtil.reset(items, this.items);
    }

    public Menu cloneWithoutItems() {
        Menu menu = new Menu(this.userType);
        clone(this, menu);
        return menu;
    }

    @Override
    public Menu clone() {
        Menu menu = cloneWithoutItems();
        this.items.forEach(item -> {
            menu.items.add(item.clone());
        });
        return menu;
    }

}