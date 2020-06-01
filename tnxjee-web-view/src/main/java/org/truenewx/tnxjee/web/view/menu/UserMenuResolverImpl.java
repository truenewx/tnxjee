package org.truenewx.tnxjee.web.view.menu;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.model.spec.user.IntegerUserIdentity;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.security.util.SecurityUtil;
import org.truenewx.tnxjee.web.view.menu.config.MenuProperties;
import org.truenewx.tnxjee.web.view.menu.model.Menu;

/**
 * 用户菜单解决器实现
 */
@Component
public class UserMenuResolverImpl implements UserMenuResolver {

    @Autowired
    private MenuProperties properties;
    @Autowired
    private MenuManager manager;

    @Override
    public Menu getUserGrantedMenu() {
        Menu fullMenu = this.manager.getFullMenu();
        if (fullMenu != null) {
            UserSpecificDetails<IntegerUserIdentity> details = SecurityUtil.getAuthorizedUserDetails();
            if (details != null && details.getIdentity().getType().equals(fullMenu.getUserType())) {
                Menu menu;
                String sessionAttributeName = this.properties.getSessionAttributeName();
                if (StringUtils.isNotBlank(sessionAttributeName)) {
                    menu = SpringWebContext.getFromSession(sessionAttributeName);
                    if (menu == null) {
                        menu = this.manager.getGrantedMenu(details.getAuthorities());
                        if (menu != null) {
                            SpringWebContext.setToSession(sessionAttributeName, menu);
                        }
                    }
                } else {
                    menu = this.manager.getGrantedMenu(details.getAuthorities());
                }
                return menu;
            }
        }
        return null;
    }

}
