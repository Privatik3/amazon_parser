package utility;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.httpclient.FiberHttpClientBuilder;
import co.paralleluniverse.strands.SuspendableRunnable;
import db.DBHandler;
import manager.ReqTaskType;
import manager.RequestTask;
import manager.Task;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
                    setMaxConnPerRoute(512).
                    setMaxConnTotal(512).build();

        } catch (Exception e) {
            log.log(Level.SEVERE, "Не удалось инициализировать HTTP Client");
            log.log(Level.SEVERE, "Exception: ", e);
        }
    }

    public static void execute(List<RequestTask> tasks) throws Exception {

//        if (false) {
//            return testMod(tasks);
//        }

        if (client == null)
            initClient();

        ArrayList<Thread> dbThreads = new ArrayList<>();
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

        ArrayList<RequestConfig> proxys;
        while (tasks.size() > 0) {
            log.info("-------------------------------------------------");
            log.info("Инициализирую новую волну, осталось: " + taskMultiply.size() + " тасков");

            if (wave != 0) {
                parseSpeed = ((parseSpeed * waveCount) + (wave - taskMultiply.size())) / ++waveCount;
                log.info("Средние количество результатов за круг: " + parseSpeed);
//                System.out.println("=============================================================");
//                System.out.println("PARSE SPEED: " + parseSpeed);
//                System.out.println("=============================================================");
            }
            wave = taskMultiply.size();

            tasks.clear();
            for (int i = 0; tasks.size() < (allProxy.size() > 512 ? 512 : allProxy.size())
                    && tasks.size() < (taskMultiply.size() * 4); i++) {
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
                        request.setConfig(proxy);

                        request.setHeader("Cookie", "session-id=147-0335730-5757324; session-id-time=2082787201l; ubid-main=134-8611924-3863705");

                        CloseableHttpResponse response = client.execute(request);
//                        System.out.println(response.getStatusLine());
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            entity = response.getEntity();
                            String body = EntityUtils.toString(entity, "UTF-8");

                            if (body.contains("CB327533540")) {

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

            cdl.await(10, TimeUnit.SECONDS);

            if (result.size() == 0 && tasks.size() != 0)
                throw new Exception("За круг было получено 0 результатов");

            taskMultiply.removeAll(result);
            if (result.size() > bufferSize || tasks.size() == 0) {
                ArrayList<RequestTask> items = new ArrayList<>(result);

                Thread dbThread = null;

                switch (items.get(0).getType()) {
                    case ITEM:
                        dbThread = new Thread(() -> DBHandler.addAmazonItems(items));
                        break;
                    case SEARCH:
                        dbThread = new Thread(() -> DBHandler.addAmazonSearch(items));
                        break;
                }

                dbThread.setDaemon(true);
                dbThread.start();
                dbThreads.add(dbThread);

                result.clear();

            }

//            if (tasks.get(0).getType() == ReqTaskType.ITEM) {
//            System.out.println("RESULT_SIZE: " + result.size());
//            int progress = (int) ((result.size() * 1.0 / initTaskSize) * 100);
//            System.out.println("SEND PROGRESS: " + progress);
//            }
        }

        log.info("-------------------------------------------------");
        log.info("Закончили парсить, затраченное время: " + (new Date().getTime() - startTime) + " ms");
        log.info("Ждём завершение кэширования данных ...");

        for (Thread thread : dbThreads)
            thread.join();


        // Кэшируем результаты для тест мода
        /*ArrayList<RequestTask> requestTasks = new ArrayList<>(result);

        for (RequestTask rTask : requestTasks) {
            Files.write(Paths.get("html/" + rTask.getId()), rTask.getHtml().getBytes());
        }*/
    }

    private static List<RequestTask> testMod(List<RequestTask> tasks) throws IOException {

        ArrayList<RequestTask> result = new ArrayList<>();
        for (RequestTask task : tasks) {
            File f = new File("html/" + task.getId());
            if (!f.exists())
                continue;

            task.setHtml(String.join("\n", Files.readAllLines(Paths.get(f.getAbsolutePath()))));
            result.add(task);
        }

        return result;
    }

    public static void closeClient() throws IOException {
        client.close();
        client = null;
    }
}
