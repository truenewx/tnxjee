package org.truenewx.tnxjee.core.version;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.core.util.StringUtil;

/**
 * 基于目录的版本号读取器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DirVersionReader extends AbstractVersionReader {

    private String root;

    private Comparator<Integer[]> comparator = new Comparator<>() {
        @Override
        public int compare(Integer[] array1, Integer[] array2) {
            int size = Math.max(array1.length, array2.length);
            for (int i = 0; i < size; i++) {
                Integer value1 = ArrayUtil.get(array1, i);
                if (value1 == null) {
                    value1 = 0;
                }
                Integer value2 = ArrayUtil.get(array2, i);
                if (value2 == null) {
                    value2 = 0;
                }
                int result = value1.compareTo(value2);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }
    };

    /**
     * @param root 版本目录所在的根目录路径
     */
    public DirVersionReader(String root) {
        Assert.notNull(root, "root must be not null");
        this.root = root;
    }

    private String getRootPath(ApplicationContext context) throws IOException {
        Resource[] locations = context.getResources(this.root);
        if (locations.length == 0) {
            throw new FileNotFoundException(this.root);
        } else if (locations.length > 1) {
            String message = "There are more than one file/dictionary for path pattern(" + this.root
                    + "):";
            for (Resource location : locations) {
                message += "\n" + location;
            }
            throw new IOException(message);
        }
        return locations[0].getURL().getPath() + Strings.SLASH;
    }

    @Override
    protected String readFullVersion(ApplicationContext context) {
        try {
            String rootPath = getRootPath(context);
            Resource[] locations = context.getResources(this.root + "/**");
            List<Integer[]> list = new ArrayList<>();
            Set<String> numbersIncludingFiles = new HashSet<>(); // 保存包含有文件的版本号
            for (Resource location : locations) {
                String path = location.getURL().getPath().substring(rootPath.length());
                if (path.endsWith(Strings.SLASH)) { // 相对路径以斜杠结尾的为目录
                    String filename = location.getFilename();
                    if (StringUtil.regexMatch(filename, "\\d+(\\.\\d+)*")) {
                        list.add(MathUtil.parseIntegerArray(filename, "\\."));
                    }
                } else { // 文件
                    int index = path.indexOf(Strings.SLASH);
                    if (index > 0) {
                        numbersIncludingFiles.add(path.substring(0, index));
                    }
                }
            }
            Collections.sort(list, this.comparator);

            Integer[] array = CollectionUtil.getLast(list, null);
            String versionNumber = getVersion(array, array.length);
            if (!numbersIncludingFiles.contains(versionNumber)) { // 版本目录下不包含文件，则需末位减一
                for (int i = array.length - 1; i >= 0; i--) {
                    if (array[i] > 0) {
                        array[i] -= 1;
                        break;
                    }
                }
            }
            return StringUtils.join(array, Strings.DOT);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return null;
    }

}
