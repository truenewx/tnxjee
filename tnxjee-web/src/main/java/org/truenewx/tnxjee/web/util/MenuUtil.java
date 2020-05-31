package org.truenewx.tnxjee.web.util;

import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.model.spec.user.IntegerUserIdentity;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.menu.model.Menu;
import org.truenewx.tnxjee.web.menu.provider.MenuProvider;
import org.truenewx.tnxjee.web.security.util.SecurityUtil;

/**
 * 菜单工具类
 */
public class MenuUtil {

    public static final String SESSION_GRANTED_MENU = "grantedMenu";

    private MenuUtil() {
    }

    /**
     * 获取当前登录用户的获权菜单
     *
     * @return 获权菜单
     */
    public static Menu getGrantedMenu() {
        UserSpecificDetails<IntegerUserIdentity> details = SecurityUtil.getAuthorizedUserDetails();
        if (details != null) {
            Menu menu = SpringWebContext.getFromSession(SESSION_GRANTED_MENU);
            if (menu == null) {
                MenuProvider menuProvider = SpringUtil.getFirstBeanByClass(SpringWebUtil.getApplicationContext(),
                        MenuProvider.class);
                if (menuProvider != null) {
                    menu = menuProvider.getGrantedMenu(details.getAuthorities());
                    SpringWebContext.setToSession(SESSION_GRANTED_MENU, menu);
                }
            }
            return menu;
        }
        return null;
    }

}
