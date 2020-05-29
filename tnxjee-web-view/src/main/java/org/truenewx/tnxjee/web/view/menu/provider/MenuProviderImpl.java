package org.truenewx.tnxjee.web.view.menu.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.web.security.access.GrantedAuthorityDecider;
import org.truenewx.tnxjee.web.security.web.access.ConfigAuthorityResolver;
import org.truenewx.tnxjee.web.view.menu.model.Menu;
import org.truenewx.tnxjee.web.view.menu.model.MenuItem;
import org.truenewx.tnxjee.web.view.menu.model.MenuLink;
import org.truenewx.tnxjee.web.view.menu.model.MenuOperation;

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

    private void cloneGrantedItemTo(Collection<? extends GrantedAuthority> grantedAuthorities,
            MenuItem item,
            List<MenuItem> items) {
        if (item instanceof MenuLink) {
            MenuLink link = (MenuLink) item;
            String href = link.getHref();
            if (StringUtils.isNotBlank(href)) {
                UserConfigAuthority configAuthority = this.authorityResolver.resolveConfigAuthority(href, HttpMethod.GET);
                // 配有href却没有相应的权限，则下级菜单项一定没有权限，忽略当前菜单项
                if (!isGranted(grantedAuthorities, configAuthority)) {
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
            if (this.authorityDecider.isGranted(grantedAuthorities, this.menu.getUserType(),
                    operation.getRank(), operation.getPermission())) {
                items.add(operation.clone());
            }
        }
    }

    private boolean isGranted(Collection<? extends GrantedAuthority> grantedAuthorities,
            UserConfigAuthority configAuthority) {
        if (configAuthority == null) {
            // 即使允许匿名访问也必须配置匿名限定，没有权限限定是不允许出现的情况，视为获权失败
            return false;
        }
        return this.authorityDecider.isGranted(grantedAuthorities, configAuthority.getType(),
                configAuthority.getRank(), configAuthority.getPermission());
    }

}
