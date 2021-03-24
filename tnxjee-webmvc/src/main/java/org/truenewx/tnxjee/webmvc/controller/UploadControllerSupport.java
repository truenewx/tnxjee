package org.truenewx.tnxjee.webmvc.controller;

import java.util.Collection;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 文件上传控制器支持
 */
public abstract class UploadControllerSupport {

    protected final void upload(MultipartHttpServletRequest request, BiConsumer<MultipartFile, Integer> consumer) {
        Collection<MultipartFile> files;
        String fileParameterName = getFileParameterName();
        if (StringUtils.isNotBlank(fileParameterName)) {
            files = request.getFiles(fileParameterName);
        } else {
            files = request.getFileMap().values();
        }
        int index = 0;
        for (MultipartFile file : files) {
            consumer.accept(file, index++);
        }
    }

    protected String getFileParameterName() {
        return null;
    }

}
