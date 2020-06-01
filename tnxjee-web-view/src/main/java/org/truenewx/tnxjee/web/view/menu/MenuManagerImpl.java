package org.truenewx.tnxjee.web.view.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.web.security.access.GrantedAuthorityDecider;
import org.truenewx.tnxjee.web.security.web.access.ConfigAuthorityResolver;
import org.truenewx.tnxjee.web.view.menu.config.MenuProperties;
import org.truenewx.tnxjee.web.view.menu.model.Menu;
import org.truenewx.tnxjee.web.view.menu.model.MenuItem;
import org.truenewx.tnxjee.web.view.menu.model.MenuLink;
import org.truenewx.tnxjee.web.view.menu.model.MenuOperation;
import org.truenewx.tnxjee.web.view.menu.parser.MenuParser;

/**
 * 菜单工厂实现
 *
 * @author jianglei
 */
@Component
public class MenuManagerImpl implements MenuManager, InitializingBean {

    private String profile;
    private MenuParser parser;
    private Menu menu;
    @Autowired
    private MenuProperties properties;
    @Autowired
    private ConfigAuthorityResolver authorityResolver;
    @Autowired
    private GrantedAuthorityDecider authorityDecider;

    @Autowired
    public void setParser(MenuParser parser) {
        this.parser = parser;
    }

    @Autowired
    public void setProfileSupplier(ProfileSupplier profileSupplier) {
        this.profile = profileSupplier.get();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource location = this.properties.getLocation();
        if (location == null) {
            location = this.parser.getDefaultLocation();
        }
        Menu menu = this.parser.parse(location);
        if (menu != null && menu.matchesProfile(this.profile)) {
            this.menu = menu;
        }
    }

    @Override
    public Menu getFullMenu() {
        return this.menu;
    }

    @Override
    public Menu getGrantedMenu(Collection<? extends GrantedAuthority> authorities) {
        if (this.menu == null) {
            return null;
        }
        Menu menu = this.menu.cloneWithoutItems();
        List<MenuItem> items = menu.getItems();
        this.menu.getItems().forEach(item -> {
            cloneGrantedItemTo(authorities, item, items);
        });
        return menu;
    }

    private void cloneGrantedItemTo(Collection<? extends GrantedAuthority> grantedAuthorities,
            MenuItem item, List<MenuItem> items) {
        if (item.matchesProfile(this.profile)) { // 首先需匹配运行环境
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
