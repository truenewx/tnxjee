package org.truenewx.tnxjee.web.view.menu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单链接
 */
public class MenuLink extends MenuItem {

    private String icon;
    private String href;
    private String target;
    private boolean assignable;
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

    public boolean isAssignable() {
        return this.assignable;
    }

    public void setAssignable(boolean assignable) {
        this.assignable = assignable;
    }

    public List<MenuItem> getSubs() {
        return this.subs;
    }

}
