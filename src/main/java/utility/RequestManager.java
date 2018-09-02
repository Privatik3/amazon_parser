package utility;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.httpclient.FiberHttpClientBuilder;
import co.paralleluniverse.strands.SuspendableRunnable;
import db.DBHandler;
import manager.ReqTaskType;
import manager.RequestTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestManager {

    private static Logger log = Logger.getLogger(RequestManager.class.getName());
    private static CloseableHttpClient client = null;
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0";

    private static void initClient() {
        try {
            TrustStrategy acceptingTrustStrategy = (certificate, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            int cores = Runtime.getRuntime().availableProcessors();

            client = FiberHttpClientBuilder.
                    create(cores * 2).
                    setUserAgent(USER_AGENT).
                    setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).
                    setSSLContext(sslContext).
                    setMaxConnPerRoute(Integer.parseInt(System.getProperty("fibers"))).
                    setMaxConnTotal(Integer.parseInt(System.getProperty("fibers"))).build();

        } catch (Exception e) {
            log.log(Level.SEVERE, "Не удалось инициализировать HTTP Client");
            log.log(Level.SEVERE, "Exception: ", e);
        }
    }

    private static List<RequestTask> returnDebugMod(ReqTaskType type) {
        switch (type) {
            case CATEGORY:
                return DBHandler.selectAllPages();
            case ITEM:
                return DBHandler.selectAllItems();
            case SEARCH_ITEM:
                return DBHandler.selectSearchItems();
            case OFFER:
                return DBHandler.selectAllOffers();
            case SEARCH:
                return DBHandler.selectAllSearchResults();
            case EBAY_CATEGORY:
                return DBHandler.selectAllEbaySearchResults();
            case EBAY_ITEM:
                return DBHandler.selectAllEbayItems();
        }

        return null;
    }

    public static List<RequestTask> execute(List<RequestTask> tasks, boolean isDebug) throws Exception {

        ReqTaskType type = tasks.get(0).getType();
//        if (type == ReqTaskType.SEARCH_ITEM) isDebug = false;

        if (isDebug) {
            return returnDebugMod(type);
        } else {
            switch (type) {
                case CATEGORY:
                    DBHandler.clearAmazonPages();
                    break;
                case ITEM:
                    DBHandler.clearAmazonItems();
                    break;
                case SEARCH_ITEM:
                    DBHandler.clearAmazonSearchItems();
                    break;
                case OFFER:
                    DBHandler.clearAmazonOffers();
                    break;
                case SEARCH:
                    DBHandler.clearAmazonSearch();
                    break;
                case EBAY_CATEGORY:
                    DBHandler.clearEbaySearch();
                    break;
                case EBAY_ITEM:
                    DBHandler.clearEbayItems();
                    break;
            }
        }

        if (client == null)
            initClient();

        HashSet<RequestTask> result = new HashSet<>();

        ArrayList<RequestConfig> allProxy = ProxyManager.getProxy();
        ArrayList<RequestConfig> goodProxy = new ArrayList<>();

        final long startTime = new Date().getTime();
        final int initTaskSize = tasks.size();
        final int bufferSize = tasks.size() < 100 ? tasks.size() : 100;

        ArrayList<RequestTask> taskMultiply = new ArrayList<>(tasks);

        Integer waveCount = 0;
        Integer parseSpeed = 0;
        Integer wave = 0;
        Integer resultStatus = 0;
        Integer failCount = 0;

        ArrayList<RequestConfig> proxys;
        while (tasks.size() > 0) {

            if (resultStatus == result.size())
                failCount = failCount + 1;
            else
                resultStatus = result.size();

            log.info("-------------------------------------------------");
            log.info("Create a new wave, left: " + taskMultiply.size() + " tasks");

            if (wave != 0) {
                parseSpeed = ((parseSpeed * waveCount) + (wave - taskMultiply.size())) / ++waveCount;
                log.info("Average download speed: " + parseSpeed);
            }
            wave = taskMultiply.size();

            tasks.clear();
            for (int i = 0; tasks.size() < (allProxy.size() > Integer.parseInt(System.getProperty("fibers")) ? Integer.parseInt(System.getProperty("fibers")) : allProxy.size())
                    && tasks.size() < (taskMultiply.size() * (taskMultiply.size() == 1 ? 1 : 4)); i++) {
                if (i == taskMultiply.size())
                    i = 0;

                tasks.add(taskMultiply.get(i));
            }

            final CountDownLatch cdl = new CountDownLatch(tasks.size());

            proxys = new ArrayList<>(goodProxy);
            goodProxy.clear();
            if (goodProxy.size() < tasks.size())
                proxys.addAll(allProxy);

            for (int i = 0; i < proxys.size() && i < tasks.size(); i++) {
                RequestTask task = tasks.get(i);
                RequestConfig proxy = proxys.get(i);

                new Fiber<Void>((SuspendableRunnable) () -> {
                    HttpEntity entity = null;
                    try {
//                        String taskUrl = URLDecoder.decode(task.getUrl(), StandardCharsets.UTF_8.toString())
//                                .replaceAll("https", "http");
                        String taskUrl = task.getUrl().replaceAll("https", "http");
                        HttpGet request = new HttpGet(taskUrl);
                        if (tasks.size() != 1)
                            request.setConfig(proxy);


//                        if (!task.getType().toString().toLowerCase().contains("ebay"))
                            request.setHeader("Cookie", System.getProperty("zipCode"));

                        CloseableHttpResponse response = client.execute(request);
//                        System.out.println(response.getStatusLine());
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            entity = response.getEntity();
                            String body = EntityUtils.toString(entity, "UTF-8");

                            if (body.contains("CB327533540") || body.contains("6e11485a66d91eff")) {
                                task.setHtml(body);
                                result.add(task);
                                goodProxy.add(proxy);
                            }
                        }
                    } catch (IOException e) {
//                        log.log(Level.SEVERE, "Ошибка внутри вайба");
//                        log.log(Level.SEVERE, "Exception: ", e);
                    } finally {
                        if (entity != null) {
                            try {
                                EntityUtils.consume(entity);
                            } catch (IOException ex) {
                                log.log(Level.SEVERE, "Не удалось освободить Entity, ресурсы заблокированы");
                                log.log(Level.SEVERE, "Exception: ", ex);
                            }
                        }
                    }
                }).start();

            }

            cdl.await(12, TimeUnit.SECONDS);

            taskMultiply.removeAll(result);
            if (resultStatus == result.size() && taskMultiply.size() != 0 && failCount > 3 ) {
                for (RequestTask task : taskMultiply)
                    Files.write(Paths.get("fail.txt"), (task.getUrl() + "\n").getBytes(), StandardOpenOption.APPEND);

                if (taskMultiply.size() > 10)
                    throw new Exception("За круг было получено 0 результатов");
                else
                    taskMultiply.clear();
            }

            if (result.size() > 0 && (result.size() > bufferSize || tasks.size() == 0)) {

                ArrayList<RequestTask> items = new ArrayList<>(result);
                result.clear();

                switch (items.get(0).getType()) {
                    case ITEM:
                        DBHandler.addAmazonItems(items);
                        break;
                    case SEARCH_ITEM:
                        DBHandler.addAmazonSearchItems(items);
                        break;
                    case SEARCH:
                        DBHandler.addAmazonSearch(items);
                        break;
                    case OFFER:
                        DBHandler.addAmazonOffers(items);
                        break;
                    case CATEGORY:
                        DBHandler.addAmazonPages(items);
                        break;
                    case EBAY_CATEGORY:
                        DBHandler.addEbaySearch(items);
                        break;
                    case EBAY_ITEM:
                        DBHandler.addEbayItems(items);
                        break;
                }

                result.clear();
            }

        }

        log.info("-------------------------------------------------");
        log.info("Finished parsing, time spent: " + (new Date().getTime() - startTime) + " ms");

        return returnDebugMod(type);
    }

    public static void closeClient() throws IOException {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
