package org.truenewx.tnxjee.web.view.menu.factory;

import org.truenewx.tnxjee.web.view.menu.model.Menu;

/**
 * 菜单工厂
 *
 * @author jianglei
 */
public interface MenuFactory {

    /**
     * 获取指定用户类型的菜单
     *
     * @param userType 用户类型
     * @return 菜单
     */
    Menu getMenu(String userType);

    /**
     * 获取默认菜单
     *
     * @return 默认菜单
     */
    default Menu getMenu() {
        return getMenu(null);
    }


}
