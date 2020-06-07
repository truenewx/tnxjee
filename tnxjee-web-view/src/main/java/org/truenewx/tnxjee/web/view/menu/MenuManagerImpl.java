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
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;
import org.truenewx.tnxjee.web.security.access.GrantedAuthorityDecider;
import org.truenewx.tnxjee.web.security.web.access.ConfigAuthorityResolver;
import org.truenewx.tnxjee.web.view.menu.config.MenuProperties;
import org.truenewx.tnxjee.web.view.menu.model.*;
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
            if (item instanceof MenuNode) {
                MenuNode node = (MenuNode) item;
                List<MenuItem> grantedSubs = new ArrayList<>();
                for (MenuItem sub : node.getSubs()) {
                    cloneGrantedItemTo(grantedAuthorities, sub, grantedSubs);
                }
                if (grantedSubs.size() > 0) { // 菜单节点的下级包含有匹配的，则该节点视为匹配
                    MenuNode grantedNode = node.cloneWithoutSubs();
                    grantedNode.setSubs(grantedSubs);
                    items.add(grantedNode);
                }
            } else if (item instanceof MenuLink) {
                MenuLink link = (MenuLink) item;
                UserConfigAuthority configAuthority = getConfigAuthority(link);
                if (isGranted(grantedAuthorities, configAuthority)) { // 权限匹配，加入目标集合
                    MenuLink grantedLink = link.cloneWithoutOperations();
                    for (MenuOperation operation : link.getOperations()) {
                        if (this.authorityDecider.isGranted(grantedAuthorities, this.menu.getUserType(), operation.getRank(), operation.getPermission())) {
                            grantedLink.getOperations().add(operation);
                        }
                    }
                    items.add(grantedLink);
                }
            }
        }
    }

    private UserConfigAuthority getConfigAuthority(MenuLink link) {
        String rank = link.getRank();
        String permission = link.getPermission();
        // 用户类型是一定在菜单中有配置的，所以不视为在菜单中配置权限的标志
        boolean menuConfigured = StringUtils.isNotBlank(rank) || StringUtils.isNotBlank(permission);
        UserConfigAuthority configAuthority = this.authorityResolver.resolveConfigAuthority(link.getPath(), HttpMethod.GET);
        // 不允许菜单配置中有权限配置，同时对应的Controller方法上也有权限配置，且两者不一致
        if (menuConfigured && configAuthority != null
                && (!StringUtil.equalsIgnoreBlank(this.menu.getUserType(), configAuthority.getType())
                || !StringUtil.equalsIgnoreBlank(rank, configAuthority.getRank())
                || !StringUtil.equalsIgnoreBlank(permission, configAuthority.getPermission()))) {
            throw new ConflictedMenuLinkConfigAuthorityException(link);
        }
        if (configAuthority != null) {
            return configAuthority;
        }
        if (menuConfigured) {
            return new UserConfigAuthority(this.menu.getUserType(), rank, permission, false);
        }
        // 两者都没有权限配置时返回null
        return null;
    }

    private boolean isGranted(Collection<? extends GrantedAuthority> grantedAuthorities,
            UserConfigAuthority configAuthority) {
        if (configAuthority == null) {
            // 即使允许匿名访问也必须配置匿名限定，没有权限限定是不允许出现的情况，视为获权失败
            return false;
        }
        return this.authorityDecider.isGranted(grantedAuthorities, configAuthority.getType(), configAuthority.getRank(),
                configAuthority.getPermission());
    }

}
