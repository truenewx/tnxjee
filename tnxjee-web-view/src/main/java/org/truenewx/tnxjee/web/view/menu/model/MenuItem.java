package org.truenewx.tnxjee.web.view.menu.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 菜单项
 */
public abstract class MenuItem extends MenuElement {

    /**
     * 选项映射集
     */
    private Map<String, Object> options = new HashMap<>();

    public Map<String, Object> getOptions() {
        return this.options;
    }

    @Override
    public abstract MenuItem clone();

    protected void clone(MenuItem source, MenuItem target) {
        super.clone(source, target);
        target.options.clear();
        target.options.putAll(source.options);
    }

}
