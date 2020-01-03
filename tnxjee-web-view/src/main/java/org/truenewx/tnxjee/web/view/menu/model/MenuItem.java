package org.truenewx.tnxjee.web.view.menu.model;

import java.util.*;

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

}
