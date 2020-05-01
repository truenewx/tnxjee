package org.truenewx.tnxjee.web.view.menu.model;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.tnxjee.core.Strings;

/**
 * 菜单
 */
public class Menu extends MenuElement {

    public static final String DEFAULT_NAME = Strings.EMPTY;

    private String userType;
    private List<MenuItem> items = new ArrayList<>();

    public Menu(String userType) {
        if (userType == null) {
            userType = DEFAULT_NAME;
        }
        this.userType = userType;
    }

    public String getUserType() {
        return this.userType;
    }

    public List<MenuItem> getItems() {
        return this.items;
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
