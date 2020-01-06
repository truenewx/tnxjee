package org.truenewx.tnxjee.web.view.menu.provider;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.web.controller.security.access.GrantedAuthorityDecider;
import org.truenewx.tnxjee.web.controller.security.web.access.ConfigAuthorityResolver;
import org.truenewx.tnxjee.web.view.menu.model.Menu;
import org.truenewx.tnxjee.web.view.menu.model.MenuItem;
import org.truenewx.tnxjee.web.view.menu.model.MenuLink;
import org.truenewx.tnxjee.web.view.menu.model.MenuOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 菜单提供者实现
 */
public class MenuProviderImpl implements MenuProvider {

    private Menu menu;
    @Autowired
    private ConfigAuthorityResolver authorityResolver;
    @Autowired
    private GrantedAuthorityDecider authorityDecider;

    public MenuProviderImpl(Menu menu) {
        this.menu = menu;
    }

    @Override
    public Menu getFullMenu() {
        return this.menu;
    }

    @Override
    public Menu getGrantedMenu(Collection<? extends GrantedAuthority> authorities) {
        Menu menu = this.menu.cloneWithoutItems();
        List<MenuItem> items = menu.getItems();
        this.menu.getItems().forEach(item -> {
            cloneGrantedItemTo(authorities, item, items);
        });
        return menu;
    }

    private void cloneGrantedItemTo(Collection<? extends GrantedAuthority> grantedAuthorities, MenuItem item,
            List<MenuItem> items) {
        if (item instanceof MenuLink) {
            MenuLink link = (MenuLink) item;
            String href = link.getHref();
            if (StringUtils.isNotBlank(href)) {
                Collection<UserConfigAuthority> configAuthorities = this.authorityResolver.resolveConfigAuthorities(href, HttpMethod.GET);
                // 配有href却没有相应的权限，则下级菜单项一定没有权限，忽略当前菜单项
                if (!isGranted(grantedAuthorities, configAuthorities)) {
                    return;
                }
            }
            // 此时href为空，或具有其访问权限

            // 获取子菜单项中具有权限的子菜单项集合
            List<MenuItem> grantedSubs = new ArrayList<>();
            link.getSubs().forEach(sub -> {
                cloneGrantedItemTo(grantedAuthorities, sub, grantedSubs);
            });
            // href为空且没有获权的子菜单项集合，则忽略当前菜单项
            if (StringUtils.isBlank(href) && grantedSubs.isEmpty()) {
                return;
            }

            MenuLink grantedLink = link.cloneWithoutSubs();
            grantedLink.getSubs().addAll(grantedSubs);
            items.add(grantedLink);
        } else if (item instanceof MenuOperation) { // 操作只需判断自身权限
            MenuOperation operation = (MenuOperation) item;
            if (this.authorityDecider.isGranted(grantedAuthorities, operation.getRole(), operation.getPermission())) {
                items.add(operation.clone());
            }
        }
    }

    private boolean isGranted(Collection<? extends GrantedAuthority> grantedAuthorities,
            Collection<UserConfigAuthority> configAuthorities) {
        if (CollectionUtils.isNotEmpty(configAuthorities)) {
            for (UserConfigAuthority configAuthority : configAuthorities) {
                // 只要有一个必备权限未获得，则视为未获权
                if (!this.authorityDecider.isGranted(grantedAuthorities, configAuthority.getRole(), configAuthority.getPermission())) {
                    return false;
                }
            }
        } // 没有权限限制，则视为已获权
        return true;
    }

}
