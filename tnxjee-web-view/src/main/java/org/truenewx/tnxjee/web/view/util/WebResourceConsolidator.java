package org.truenewx.tnxjee.web.view.util;

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.core.spring.beans.ContextInitializedBean;

import com.extjs.JsBuilder;

/**
 * WEB资源压缩合并器<br/>
 * 针对CSS/JS文件进行压缩合并处理
 *
 * @author liuzhiyi
 */
public class WebResourceConsolidator implements ContextInitializedBean {

    /**
     * 配置文件路径
     */
    private String configFileName = "JsBuilder.jsb2";

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        Resource resource = context.getResource(this.configFileName);
        if (resource.exists()) {
            File file = resource.getFile();
            String projectFile = file.getAbsolutePath();
            String homeDir = file.getParentFile().getAbsolutePath();
            JsBuilder.build(homeDir, projectFile);
        }
    }
}
