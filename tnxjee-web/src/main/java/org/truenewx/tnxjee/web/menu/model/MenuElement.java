package org.truenewx.tnxjee.web.menu.model;

import java.io.Serializable;
import java.util.*;

/**
 * 菜单元素
 */
public abstract class MenuElement implements Cloneable, Serializable {

    private static final long serialVersionUID = -3827849614788066392L;
    
    /**
     * 显示名称映射集
     */
    private Map<Locale, String> captions = new HashMap<>();
    /**
     * 描述映射集
     */
    private Map<Locale, String> descs = new HashMap<>();
    /**
     * 可见环境集合
     */
    private Set<String> profiles = new HashSet<>();

    public Map<Locale, String> getCaptions() {
        return this.captions;
    }

    public Map<Locale, String> getDescs() {
        return this.descs;
    }

    public Set<String> getProfiles() {
        return this.profiles;
    }

    public void setCaption(String caption) {
        this.captions.put(Locale.getDefault(), caption);
    }

    public String getCaption() {
        return this.captions.get(Locale.getDefault());
    }

    public void setDesc(String desc) {
        this.descs.put(Locale.getDefault(), desc);
    }

    public String getDesc() {
        return this.descs.get(Locale.getDefault());
    }

    protected void clone(MenuElement source, MenuElement target) {
        target.captions.clear();
        target.captions.putAll(source.getCaptions());
        target.descs.clear();
        target.descs.putAll(source.descs);
        target.profiles.clear();
        target.profiles.addAll(source.profiles);
    }
}
