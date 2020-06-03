package org.truenewx.tnxjee.web.view.menu.model;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.tnxjee.core.util.CollectionUtil;

/**
 * 菜单节点
 */
public class MenuNode extends MenuItem {

    private static final long serialVersionUID = 3645843518779941539L;

    private List<MenuItem> subs = new ArrayList<>();

    public List<MenuItem> getSubs() {
        return this.subs;
    }

    public void setSubs(List<MenuItem> subs) {
        CollectionUtil.reset(subs, this.subs);
    }

    public MenuNode cloneWithoutSubs() {
        MenuNode node = new MenuNode();
        clone(this, node);
        return node;
    }

    @Override
    public MenuNode clone() {
        MenuNode node = cloneWithoutSubs();
        this.subs.forEach(sub -> {
            node.subs.add(sub.clone());
        });
        return node;
    }

}
