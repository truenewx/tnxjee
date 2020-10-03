package org.truenewx.tnxjee.webmvc.view.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.model.spec.user.IntegerUserIdentity;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.model.spec.user.security.UserSpecificDetails;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.webmvc.security.util.SecurityUtil;
import org.truenewx.tnxjee.webmvc.security.web.access.ConfigAuthorityResolver;
import org.truenewx.tnxjee.webmvc.view.menu.config.MenuProperties;
import org.truenewx.tnxjee.webmvc.view.menu.model.*;

/**
 * 用户菜单解决器实现
 */
@Component
public class UserMenuResolverImpl implements UserMenuResolver {

    @Autowired
    private MenuProperties properties;
    @Autowired
    private MenuManager manager;
    @Autowired
    private ConfigAuthorityResolver authorityResolver;

    @Override
    public Menu getUserGrantedMenu() {
        Menu fullMenu = this.manager.getFullMenu();
        if (fullMenu != null) {
            UserSpecificDetails<IntegerUserIdentity> details = SecurityUtil.getAuthorizedUserDetails();
            if (details != null && Objects.equals(details.getIdentity().getType(), fullMenu.getUserType())) {
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

    @Override
    public List<Integer> indexesOf(@NonNull String path) {
        List<Integer> indexes = new ArrayList<>();
        Menu menu = getUserGrantedMenu();
        // 有访问权限的链接才需要查找索引
        if (menu != null) {
            UserConfigAuthority configAuthority = this.authorityResolver.resolveConfigAuthority(path, HttpMethod.GET);
            List<MenuItem> items = menu.getItems();
            applyIndex(items, configAuthority, path, indexes);
        }
        return indexes;
    }

    private void applyIndex(List<MenuItem> items, UserConfigAuthority configAuthority, String path,
            List<Integer> indexes) {
        for (int i = 0; i < items.size(); i++) {
            MenuItem item = items.get(i);
            if (item instanceof MenuLink) {
                MenuLink link = (MenuLink) item;
                if (link.getPath().equals(path)) {
                    indexes.add(i);
                    return; // 找到匹配的直接返回
                }
                // 不匹配则尝试比较所有操作
                for (MenuOperation operation : link.getOperations()) {
                    if (StringUtil.equalsIgnoreBlank(configAuthority.getRank(), operation.getRank())
                            && StringUtil
                            .equalsIgnoreBlank(configAuthority.getPermission(), operation.getPermission())) {
                        // 菜单链接下有操作的权限与地址所需权限匹配，则视为菜单链接匹配
                        indexes.add(i);
                        return; // 找到匹配的直接返回
                    }
                }
            } else if (item instanceof MenuNode) {
                MenuNode node = (MenuNode) item;
                List<MenuItem> subs = node.getSubs();
                applyIndex(subs, configAuthority, path, indexes);
                if (indexes.size() > 0) {
                    indexes.add(0, i);
                }
            }
        }
    }

}
