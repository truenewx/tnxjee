package org.truenewx.tnxjee.core.region.address;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.truenewx.tnxjee.core.net.InetAddressSet;
import org.truenewx.tnxjee.core.util.IOUtil;

/**
 * 网络地址->区划解决器实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class InetAddressRegionResolverImpl implements InetAddressRegionResolver {
    private RegionInetAddressSetMapSource regionInetAddressSetMapSource;
    private Properties cache = new Properties();
    private File cacheFile;
    private int storeCachePerSize = 10;

    public void setRegionInetAddressSetMapSource(
            RegionInetAddressSetMapSource regionInetAddressSetMapSource) {
        this.regionInetAddressSetMapSource = regionInetAddressSetMapSource;
    }

    public void setCacheFile(File cacheFile) throws IOException {
        if (!cacheFile.exists()) {
            IOUtil.createFile(cacheFile);
        }
        this.cacheFile = cacheFile;
        loadCache();
    }

    /**
     * @param storeCachePerSize 每多少个内存缓存保存缓存文件一次，默认为10
     */
    public void setStoreCachePerSize(int storeCachePerSize) {
        this.storeCachePerSize = storeCachePerSize;
    }

    private void loadCache() {
        if (this.cacheFile != null) {
            this.cache.clear();
            try {
                this.cache.load(new FileInputStream(this.cacheFile));
            } catch (IOException e) {
                LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            }
        }
    }

    private void storeCache() {
        if (this.cacheFile != null) {
            try {
                this.cache.store(new FileOutputStream(this.cacheFile), null);
            } catch (IOException e) {
                LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            }
        }
    }

    @Override
    public String resolveRegionCode(InetAddress address) {
        String ip = address.getHostAddress();
        String region = (String) this.cache.get(ip);
        if (region == null) {
            Map<String, InetAddressSet> map = this.regionInetAddressSetMapSource.getMap();
            if (map != null) {
                for (Entry<String, InetAddressSet> entry : map.entrySet()) {
                    if (entry.getValue().contains(address)) {
                        region = entry.getKey();
                        this.cache.put(ip, region);
                        if (this.cache.size() % this.storeCachePerSize == 0) {
                            storeCache();
                        }
                        break;
                    }
                }
            }
        }
        return region;
    }

}
