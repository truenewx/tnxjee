package org.truenewx.tnxjee.web.menu.factory;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.web.menu.model.Menu;
import org.truenewx.tnxjee.web.menu.parser.MenuParser;

/**
 * 菜单工厂实现
 *
 * @author jianglei
 */
public class MenuFactoryImpl implements MenuFactory, InitializingBean {

    private MenuParser parser;
    private Resource[] resources;
    private Map<String, Menu> menus = new HashMap<>();

    @Override
    public Menu getMenu(String userType) {
        if (userType == null) {
            userType = Menu.DEFAULT_NAME;
        }
        return this.menus.get(userType);
    }

    @Autowired
    public void setParser(MenuParser parser) {
        this.parser = parser;
    }

    public void setResources(Resource... resources) {
        this.resources = resources;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (ArrayUtils.isEmpty(this.resources)) {
            this.resources = new Resource[]{ this.parser.getDefaultConfig() };
        }
        for (Resource resource : this.resources) {
            Menu menu = this.parser.parse(resource);
            this.menus.put(menu.getUserType(), menu);
        }
    }
}
