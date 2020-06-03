package org.truenewx.tnxjee.web.view.menu;

import org.truenewx.tnxjee.web.view.menu.model.MenuLink;

/**
 * 冲突的菜单链接配置权限异常
 */
public class ConflictedMenuLinkConfigAuthorityException extends RuntimeException {

    private static final long serialVersionUID = -5939771245945062923L;

    public ConflictedMenuLinkConfigAuthorityException(MenuLink link) {
        super("Conflicted config authority: " + link.getHref());
    }

}
