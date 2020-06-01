package org.truenewx.tnxjee.web.view.menu;

import org.truenewx.tnxjee.web.view.menu.model.Menu;

/**
 * 用户菜单解决器
 */
public interface UserMenuResolver {

    /**
     * 获取当前用户获权的菜单
     *
     * @return 当前用户获权的菜单
     */
    Menu getUserGrantedMenu();

}
