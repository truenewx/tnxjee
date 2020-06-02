package org.truenewx.tnxjee.web.view.menu.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单链接
 */
public class MenuLink extends MenuItem {

    private static final long serialVersionUID = 5574091037889165424L;

    private String icon;
    private String href;
    private String target;
    private boolean dynamic;
    private List<MenuItem> subs = new ArrayList<>();

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isDynamic() {
        return this.dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public List<MenuItem> getSubs() {
        return this.subs;
    }

    public List<MenuLink> getSubLinks() {
        return this.subs.stream().filter(sub -> sub instanceof MenuLink).map(sub -> (MenuLink) sub)
                .collect(Collectors.toList());
    }

    public List<MenuOperation> getSubOperations() {
        return this.subs.stream().filter(sub -> sub instanceof MenuOperation).map(sub -> (MenuOperation) sub)
                .collect(Collectors.toList());
    }

    public MenuLink cloneWithoutSubs() {
        MenuLink link = new MenuLink();
        link.icon = this.icon;
        link.href = this.href;
        link.target = this.target;
        link.dynamic = this.dynamic;
        clone(this, link);
        return link;
    }

    @Override
    public MenuLink clone() {
        MenuLink link = cloneWithoutSubs();
        this.subs.forEach(sub -> {
            link.subs.add(sub.clone());
        });
        return link;
    }
}
