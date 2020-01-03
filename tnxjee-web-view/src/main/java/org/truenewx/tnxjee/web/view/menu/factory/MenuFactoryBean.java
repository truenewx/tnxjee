package org.truenewx.tnxjee.web.view.menu.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.web.view.menu.model.Menu;

/**
 * 菜单工厂Bean
 */
public class MenuFactoryBean implements FactoryBean<Menu> {

    private String menuName;
    private MenuFactory menuFactory;

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    @Autowired
    public void setMenuFactory(MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return Menu.class;
    }

    @Override
    public Menu getObject() throws Exception {
        return this.menuFactory.getMenu(this.menuName);
    }
}
