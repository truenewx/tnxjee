package org.truenewx.tnxjee.web.view.menu.model;

import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spec.Named;

import java.util.ArrayList;
import java.util.Collection;
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

    /**
     * 获取已取得指定授权的菜单项清单
     *
     * @param authorities 已获授权集
     * @return 已取得指定授权的菜单项清单
     */
    public List<MenuItem> getGrantedItems(Collection<? extends GrantedAuthority> authorities) {
        List<MenuItem> grantedItems = new ArrayList<>();
        return grantedItems;
    }

}
