package org.truenewx.tnxjee.web.view.menu.factory;

import org.truenewx.tnxjee.web.view.menu.model.Menu;

/**
 * 菜单工厂
 *
 * @author jianglei
 */
public interface MenuFactory {

    /**
     * 获取指定名称的菜单
     *
     * @param name 菜单名
     * @return 菜单
     */
    Menu getMenu(String name);

    /**
     * 获取默认菜单
     *
     * @return 默认菜单
     */
    default Menu getMenu() {
        return getMenu(null);
    }


}
