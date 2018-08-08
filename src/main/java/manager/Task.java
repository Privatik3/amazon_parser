package manager;

import db.DBHandler;
import excel.Handler;
import face.Filter;
import face.InterfaceParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import parser.*;
import utility.RequestManager;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Task extends Thread {

    private static Logger log = Logger.getLogger(Task.class.getName());
    private static PrintStream err;

    private Integer status = 0;
    private InterfaceParams params;
    private List<AmazonItem> result = new ArrayList<>();

    public Task(InterfaceParams parameters) {
        this.params = parameters;
        this.setDaemon(true);

        err = System.err;
        System.setErr(null);
    }

    @Override
    public void run() {

        try {
            long time = new Date().getTime();

            log.info("Инициализируем очистку всех кэшей");
            DBHandler.clearAll();

            List<RequestTask> reqTasks = new ArrayList<>();
            if (!params.getUrlListing().isEmpty()) {
                log.info("-------------------------------------------------");
                log.info("Формируем список ссылок на страницы каталога");
                reqTasks = initCatalogReq(params.getUrlListing());
                RequestManager.execute(reqTasks);
                reqTasks.clear();

                log.info("-------------------------------------------------");
                log.info("Обрабатываем асины, полеченные по ссылке пользователя");
                reqTasks.addAll(convertToTasks(Amazon.parseCategory(DBHandler.selectAllPages())));
            }

            if (!params.getPathToListing().isEmpty())
                reqTasks.addAll(convertToTasks(Handler.readListOfAsin(params.getPathToListing())));

            if (reqTasks.size() == 0)
                throw new Exception("Не было получено ни одного ASIN для обработки");

            log.info("Выполняем запрос на выкачку листинга");
            RequestManager.execute(reqTasks);
            reqTasks.clear();

            result = Amazon.parseItems(DBHandler.selectAllItems());

            log.info("-------------------------------------------------");
            log.info("Выполняем фильтрацию полученных результатов");
            filterResult();
            log.info("Фильтрация завершена, осталось " + result.size() + " позиций");

            log.info("-------------------------------------------------");
            log.info("Формируем и выкачиваем поисковые запросы");


            for (AmazonItem item : result) {
                Integer num = 0;
                for (String req : item.getSearchReq()) {
                    RequestTask task = new RequestTask(item.getAsin() + ":" + String.valueOf(num++));
                    task.setUrl("https://www.amazon.com/s?field-keywords=" + URLEncoder.encode(req, "UTF-8"));
                    task.setType(ReqTaskType.SEARCH);

                    reqTasks.add(task);
                }
            }

            RequestManager.execute(reqTasks);
            reqTasks.clear();
            List<AmazonSearch> searchResult = Amazon.parseSearchReq(DBHandler.selectAllSearchResults());

            log.info("-------------------------------------------------");
            log.info("Докачиваем товары полученные после поиска");
            reqTasks = getTaskFromSearchResult(searchResult);
            DBHandler.clearAmazonItems();

            RequestManager.execute(reqTasks);
            reqTasks.clear();

            List<AmazonItem> searchItems = Amazon.parseItems(DBHandler.selectAllItems());

            log.info("-------------------------------------------------");
            log.info("Сливаем полеченный результат");
            for (AmazonItem item : result) {
                if (item.getSearchReq().size() == 0)
                    continue;

                ArrayList<ItemShortInfo> searchInfo = new ArrayList<>();

                List<AmazonSearch> search = searchResult.stream()
                        .filter(x -> x.getRelatedAsin().equals(item.getAsin()))
                        .collect(Collectors.toList());

                HashSet<String> asins = new HashSet<>();
                search.forEach(s -> asins.addAll(s.getAsins()));

                for (String asin : asins) {
                    Optional<AmazonItem> first = searchItems.stream()
                            .filter(x -> x.getAsin().equals(asin))
                            .findFirst();

                    if (first.isPresent()) {
                        addInfo(searchInfo, asin, first);
                    } else {
                        first = result.stream()
                                .filter(x -> x.getAsin().equals(asin))
                                .findFirst();

                        if (first.isPresent()) {
                            addInfo(searchInfo, asin, first);
                        }
                    }
                }

                item.setSearchInfo(searchInfo);
            }
            searchItems.clear();
            searchResult.clear();

            log.info("Формируем список запросов на выкачку оферов");
            HashSet<String> asins = new HashSet<>();
            for (AmazonItem item : result) {
                if (item.getAvailability() && item.getNew())
                    asins.add(item.getAsin());

                for (ItemShortInfo info : item.getSearchInfo())
                    if (info.getAvailability() && info.getIsNew())
                        asins.add(info.getAsin());
            }

            reqTasks = convertToOfferTasks(asins);
            asins.clear();

            RequestManager.execute(reqTasks);
            reqTasks.clear();

            List<ItemOffer> offers = Amazon.parseOffers(DBHandler.selectAllOffers());
            log.info("-------------------------------------------------");
            log.info("Заполняем оферы в результирующей выборке");

            for (AmazonItem item : result) {
                String mainAsin = item.getAsin();

                Optional<ItemOffer> mainOffer = offers.stream().filter(offer -> offer.getAsin().equals(mainAsin)).findFirst();
                mainOffer.ifPresent(itemOffer -> item.setOffers(itemOffer.getOffers()));

                for (ItemShortInfo info : item.getSearchInfo()) {
                    String searchAsin = info.getAsin();

                    Optional<ItemOffer> shortOffer = offers.stream().filter(offer -> offer.getAsin().equals(searchAsin)).findFirst();
                    if (shortOffer.isPresent() && shortOffer.get().getOffers().size() > 0)
                        info.setFirstOffer(shortOffer.get().getOffers().get(0));
                }

            }
            offers.clear();

            status = 100;

            log.info("-------------------------------------------------");
            log.info("ПОЛНОЕ ВРЕМЯ ВЫПОЛНЕНИЯ: " + (new Date().getTime() - time) + " ms");
            log.info("-------------------------------------------------");

            DBHandler.close();
        } catch (Exception e) {
            log.info("-------------------------------------------------");
            log.log(Level.SEVERE, "Ошибка во время выполнения таска, закрываем такс");
            log.log(Level.SEVERE, "Exception: " + e.getMessage());

            System.setErr(err);
            e.printStackTrace();
        }

    }

    private List<RequestTask> initCatalogReq(String url) {

        List<RequestTask> tasks = new ArrayList<>();

        int maxPages = 1;
        try {

            Document doc = Jsoup.connect(url).get();
            String pages = doc.select("div#pagn").text();

            if (pages.length() > 0) {
                pages = pages.substring(0, pages.lastIndexOf("Next")).trim();
                String[] allPages = pages.split(" ");
                maxPages = Integer.parseInt(allPages[allPages.length - 1]);
            }
        } catch (IOException e) {
            log.info("-------------------------------------------------");
            log.log(Level.SEVERE, "Не удалось загрузить сраницы");
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        }

        for (int i = 1; i <= (maxPages > 100 ? 100 : maxPages); i++) {
            RequestTask task = new RequestTask(String.valueOf(i));
            task.setUrl( url + "&page=" + i);
            task.setType(ReqTaskType.CATEGORY);

            tasks.add(task);
        }

        return tasks;
    }

    private void addInfo(ArrayList<ItemShortInfo> searchInfo, String asin, Optional<AmazonItem> first) {
        ItemShortInfo info = new ItemShortInfo();
        info.setAsin(asin);
        info.setAvailability(first.get().getAvailability());
        info.setIsNew(first.get().getNew());

        searchInfo.add(info);
    }

    private List<RequestTask> convertToOfferTasks(HashSet<String> asins) {

        List<RequestTask> reqTasks = new ArrayList<>();
        for (String asin : asins) {
            RequestTask task = new RequestTask(asin);
            task.setUrl("https://www.amazon.com/gp/offer-listing/" + asin + "?f_new=true");
            task.setType(ReqTaskType.OFFER);
            reqTasks.add(task);
        }

        return reqTasks;
    }

    private List<RequestTask> getTaskFromSearchResult(List<AmazonSearch> searchResult) {
        List<RequestTask> reqTasks;
        HashSet<String> asins = new HashSet<>();
        searchResult.forEach(e -> asins.addAll(e.getAsins()));

        Iterator<String> asinIter = asins.iterator();
        while (asinIter.hasNext()) {

            String asin = asinIter.next();
            Optional<AmazonItem> first = result.stream()
                    .filter(x -> x.getAsin().equals(asin))
                    .findFirst();

            if (first.isPresent())
                asinIter.remove();
        }

        reqTasks = convertToTasks(asins);
        asins.clear();
        return reqTasks;
    }

    private void filterResult() {
        Iterator<AmazonItem> iterator = result.iterator();

        out:
        while (iterator.hasNext()) {
            AmazonItem item = iterator.next();

            for (Filter filter : params.getFilters()) {
                if (!filter.getEnable()) continue;

                switch (filter.getType()) {
                    case NONE:
                        if ((item.getAvailability() && !item.getPromoOffer()) &&
                                (item.getbSR() < filter.getMin() || item.getbSR() > filter.getMax()))
                            iterator.remove();

                        continue out;
                    case PRIME:
                        if (item.getPromoOffer() &&
                                (item.getbSR() < filter.getMin() || item.getbSR() > filter.getMax()))
                            iterator.remove();

                        continue;
                    case UNAVALIABLE:
                        if (!item.getAvailability() &&
                                (item.getbSR() < filter.getMin() || item.getbSR() > filter.getMax()))
                            iterator.remove();

                        continue out;
                    case RATING:
                        if (item.getRating() < filter.getMin() || item.getRating() > filter.getMax())
                            iterator.remove();

                        continue out;
                    case CREATION_DATE:
                        System.out.println(item.getDateFirstAvailable());
                        double dateCreation = (double) item.getDateFirstAvailable().getTime();
                        if (dateCreation < filter.getMin()
                                || dateCreation > filter.getMax())
                            iterator.remove();

                        continue out;
                }
            }
        }
    }

    private List<RequestTask> convertToTasks(Collection<String> asins) {

//        log.info("Формирую ссылки на листинги");
        List<RequestTask> result = new ArrayList<>();
        for (String asin : asins) {
            RequestTask task = new RequestTask(asin);
            task.setUrl("https://www.amazon.com/dp/" + asin);
            task.setType(ReqTaskType.ITEM);

            result.add(task);
        }

        return result;
    }

    private Collection<? extends RequestTask> queryCategory() {

        log.info("Найдена ссылка на листинг, начинаю выкачку");
        //TODO Реализовать метод
        return new ArrayList<>();
    }

    public Integer status() {
        return status;
    }

    public List<AmazonItem> getResult() {
        return this.result;
    }
}
