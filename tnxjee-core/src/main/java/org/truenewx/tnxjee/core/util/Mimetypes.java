package org.truenewx.tnxjee.core.util;

import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.truenewx.tnxjee.core.Strings;

public class Mimetypes {

    public static final String DEFAULT_MIMETYPE = "application/octet-stream";

    private static Mimetypes INSTANCE = null;

    private HashMap<String, String> extensionToMimetypeMap = new HashMap<>();

    private Mimetypes() {
    }

    public synchronized static Mimetypes getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new Mimetypes();
        InputStream in = INSTANCE.getClass().getResourceAsStream("/mime.types");
        if (in != null) {
            try {
                INSTANCE.loadMimetypes(in);
            } catch (IOException e) {
                LogUtil.error(Mimetypes.class, e);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    LogUtil.error(Mimetypes.class, ex);
                }
            }
        }
        return INSTANCE;
    }

    public void loadMimetypes(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0 && !line.startsWith("#")) {
                StringTokenizer st = new StringTokenizer(line, " \t");
                if (st.countTokens() > 1) {
                    String extension = st.nextToken();
                    if (st.hasMoreTokens()) {
                        String mimetype = st.nextToken();
                        this.extensionToMimetypeMap.put(extension.toLowerCase(), mimetype);
                    }
                }
            }
        }
    }

    private String getMimetypeByExtension(String fileName) {
        String extension = fileName;
        int index = fileName.lastIndexOf(Strings.DOT);
        if (0 <= index && index + 1 < fileName.length()) {
            extension = fileName.substring(index + 1);
        }
        extension = extension.toLowerCase();
        if (this.extensionToMimetypeMap.containsKey(extension)) {
            return this.extensionToMimetypeMap.get(extension);
        }
        return null;
    }

    public String getMimetype(String fileName) {
        String mimeType = getMimetypeByExtension(fileName);
        if (mimeType != null) {
            return mimeType;
        }
        return DEFAULT_MIMETYPE;
    }

    public String getMimetype(File file) {
        return getMimetype(file.getName());
    }
}
