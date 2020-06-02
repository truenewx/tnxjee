package org.truenewx.tnxjee.web.view.menu.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<MenuLink> getLinks() {
        return this.items.stream().filter(item -> item instanceof MenuLink)
                .map(item -> (MenuLink) item).collect(Collectors.toList());
    }

    public List<MenuOperation> getOperations() {
        return this.items.stream().filter(item -> item instanceof MenuOperation)
                .map(item -> (MenuOperation) item).collect(Collectors.toList());
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
