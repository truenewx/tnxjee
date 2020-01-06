package org.truenewx.tnxjee.web.view.menu.provider;

import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.web.view.menu.model.Menu;

import java.util.Collection;

/**
 * 菜单提供者
 */
public interface MenuProvider {

    /**
     * 获取包含全部内容的完整菜单
     *
     * @return
     */
    Menu getFullMenu();

    /**
     * 获取内容与指定授权匹配的菜单，是完整菜单的子集
     *
     * @param authorities 所获权限集
     * @return 获权菜单
     */
    Menu getGrantedMenu(Collection<? extends GrantedAuthority> authorities);

}
