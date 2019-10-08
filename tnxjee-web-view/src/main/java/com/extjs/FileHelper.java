package com.extjs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * 文件操作帮助类
 *
 * @author liuzhiyi
 */
class FileHelper {

    private FileHelper() {
    }

    public static String readFileToString(File file) {
        try {
            FileInputStream in = new FileInputStream(file);
            byte bt[] = new byte[(int) file.length()];
            in.read(bt);
            String s = new String(bt, "UTF-8");
            in.close();
            return s;
        } catch (Exception ex) {
        }
        return null;
    }

    public static void writeStringToFile(String s, File file, Boolean append) {
        try {
            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file, append),
                    "UTF-8");
            fw.write(s, 0, s.length());
            fw.flush();
            fw.close();
        } catch (Exception ex) {
        }
    }

    public static String insertFileSuffix(String fileName, String suffix) throws Exception {
        int endOfDot = fileName.lastIndexOf(".");
        if (endOfDot == -1) {
            throw new Exception("No period in the target file output.");
        }
        return fileName.substring(0, endOfDot) + suffix + fileName.substring(endOfDot);
    }

    public static void copyDirectory(File sourceLocation, File targetLocation, String regExPattern)
            throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list(new FilenameFilter() {
                private Pattern pattern = Pattern.compile(regExPattern);

                @Override
                public boolean accept(File dir, String name) {
                    File newFile = new File(dir.getAbsolutePath() + File.separatorChar + name);
                    Boolean isSvn = (newFile.getAbsolutePath().indexOf(".svn") != -1);
                    Boolean isHidden = newFile.isHidden();
                    Boolean isDir = newFile.isDirectory();
                    Boolean matches = this.pattern.matcher(name).matches();
                    if (isSvn || isHidden) {
                        return false;
                    } else if (isDir) {
                        return true;
                    } else {
                        return matches;
                    }
                }
            });

            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]), regExPattern);
            }
        } else {
            Boolean isSvn = (sourceLocation.getAbsolutePath().indexOf(".svn") != -1);
            Boolean isHidden = sourceLocation.isHidden();
            if (isSvn || isHidden) {
                return;
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public static File[] listFilesAsArray(File directory, FilenameFilter filter, boolean recurse) {
        Collection<File> files = listFiles(directory, filter, recurse);

        File[] arr = new File[files.size()];
        return files.toArray(arr);
    }

    public static Collection<File> listFiles(File directory, FilenameFilter filter,
            boolean recurse) {
        Vector<File> files = new Vector<>();

        // Get files / directories in the directory
        File[] entries = directory.listFiles();

        // Go over entries
        for (File entry : entries) {
            // If there is no filter or the filter accepts the
            // file / directory, add it to the list
            if (filter == null || filter.accept(directory, entry.getName())) {
                files.add(entry);
            }

            // If the file is a directory and the recurse flag
            // is set, recurse into the directory
            if (recurse && entry.isDirectory()) {
                files.addAll(listFiles(entry, filter, recurse));
            }
        }
        // Return collection of files
        return files;
    }

}
