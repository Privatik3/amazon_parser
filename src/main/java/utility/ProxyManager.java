package utility;

import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProxyManager {

    private static ArrayList<RequestConfig> PROXIES = new ArrayList<>();

    private static void loadProxies() {
        try {
            List<String> proxyApi = Files.readAllLines(Paths.get("proxy-list.txt"));

            for (String proxy : proxyApi) {
                try {
                    String hostName = proxy.split(":")[0];
                    int port = Integer.parseInt(proxy.split(":")[1]);

                    PROXIES.add(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                            .setProxy(new HttpHost(hostName, port, "http"))
                            .setConnectionRequestTimeout(8 * 1000)
                            .setSocketTimeout(8 * 1000)
                            .setConnectTimeout(8 * 1000).build());
                } catch (Exception ignore) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<RequestConfig> getProxy() {
        if (PROXIES.size() == 0)
            loadProxies();

        return PROXIES;
    }

    public static void clear() {
        PROXIES.clear();
    }
}
