package org.truenewx.tnxjee.web.view.menu.model;

/**
 * 菜单项
 */
public abstract class MenuItem extends MenuElement {

    private static final long serialVersionUID = 1338297288402064073L;

    private String icon;

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public abstract MenuItem clone();

    protected void clone(MenuItem source, MenuItem target) {
        super.clone(source, target);
        target.icon = source.icon;
    }

}
