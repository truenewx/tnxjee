package org.truenewx.tnxjee.model.spec;

import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.Mimetypes;

/**
 * 文件上传限制
 *
 * @author jianglei
 */
public class FileUploadLimit {

    private int number;
    private long capacity;
    private boolean extensionsRejected;
    private String[] extensions;
    private String[] mimeTypes;
    private boolean imageable;
    private Boolean croppable;
    private FlatSize[] sizes;

    public FileUploadLimit(int number, long capacity, boolean extensionsRejected, String... extensions) {
        Assert.isTrue(number >= 0, "number must be not less than 0");
        this.number = number;
        Assert.isTrue(capacity >= 0, "capacity must be not less than 0");
        this.capacity = capacity;
        this.extensionsRejected = extensionsRejected;
        this.extensions = extensions;
        if (!this.extensionsRejected) {
            this.mimeTypes = new String[extensions.length];
            Mimetypes mimetypes = Mimetypes.getInstance();
            for (int i = 0; i < extensions.length; i++) {
                this.mimeTypes[i] = mimetypes.getMimetype(extensions[i]);
            }
        }
    }

    public FileUploadLimit(int number, long capacity, String... extensions) {
        this(number, capacity, false, extensions);
    }

    public int getNumber() {
        return this.number;
    }

    public long getCapacity() {
        return this.capacity;
    }

    public boolean isExtensionsRejected() {
        return this.extensionsRejected;
    }

    public String[] getExtensions() {
        return this.extensions;
    }

    public String[] getMimeTypes() {
        return this.mimeTypes;
    }

    public boolean isImageable() {
        return this.imageable;
    }

    public Boolean getCroppable() {
        return this.croppable;
    }

    public FlatSize[] getSizes() {
        return this.sizes;
    }

    public void enableImage(boolean croppable, FlatSize... sizes) {
        this.imageable = true;
        this.croppable = croppable;
        this.sizes = sizes;
    }

}
