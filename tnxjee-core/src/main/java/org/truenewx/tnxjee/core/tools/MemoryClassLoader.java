package org.truenewx.tnxjee.core.tools;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

public class MemoryClassLoader extends URLClassLoader {

    private Map<String, MemoryJavaFileManager.OutputMemoryJavaFileObject> fileObjects;

    public MemoryClassLoader(
            Map<String, MemoryJavaFileManager.OutputMemoryJavaFileObject> fileObjects) {
        super(new URL[0]);
        this.fileObjects = fileObjects;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        MemoryJavaFileManager.OutputMemoryJavaFileObject fileObject = this.fileObjects.remove(name);
        if (fileObject != null) {
            byte[] bytes = fileObject.getBytes();
            return defineClass(name, bytes, 0, bytes.length);
        }
        return super.findClass(name);
    }
}
