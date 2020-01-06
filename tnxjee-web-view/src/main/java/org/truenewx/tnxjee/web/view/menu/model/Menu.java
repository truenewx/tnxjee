package org.truenewx.tnxjee.web.view.menu.model;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.Named;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单
 */
public class Menu extends MenuElement implements Named {

    public static final String DEFAULT_NAME = Strings.EMPTY;

    private String name;
    private List<MenuItem> items = new ArrayList<>();

    public Menu(String name) {
        if (name == null) {
            name = DEFAULT_NAME;
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<MenuItem> getItems() {
        return this.items;
    }

    public Menu cloneWithoutItems() {
        Menu menu = new Menu(this.name);
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
