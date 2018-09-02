package manager;

import db.DBHandler;
import excel.Handler;
import face.Filter;
import face.InterfaceParams;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import parser.*;
import utility.RequestManager;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;
import java.util.stream.Collectors;

public class Task extends Thread {

    private static Logger log = Logger.getLogger("");

    private static Boolean DEBUG_MOD = true;

    private Integer status = 0;
    private InterfaceParams params;
    private List<AmazonItem> result = new ArrayList<>();

    public Task(InterfaceParams parameters) {
        this.params = parameters;
        this.setDaemon(true);

        System.setProperty("addPrice", String.valueOf(params.getAddToPrice()));
        System.setProperty("fibers", String.valueOf(params.getCountOfFibers()));

        if (DEBUG_MOD) {
            java.util.logging.Handler[] handlers = log.getHandlers();
            log.removeHandler(handlers[0]);

            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()) + " -> " +
                                    record.getMessage() + "\r\n";
                }
            });
            log.addHandler(handler);
        }
    }

    @Override
    public void run() {

        try {
            long time = new Date().getTime();

            if (!DEBUG_MOD)
                DBHandler.clearAll();

            log.info("Updating zip code");
            if (!DEBUG_MOD)
                System.setProperty("zipCode", updateZipCode(params.getUsaZipCode()));

            List<RequestTask> reqTasks = new ArrayList<>();
            if (!params.getUrlListing().isEmpty()) {
                log.info("-------------------------------------------------");
                log.info("Формируем список ссылок на страницы каталога");
                reqTasks = initCatalogReq(params.getUrlListing());
                List<RequestTask> pages = RequestManager.execute(reqTasks, DEBUG_MOD);
                reqTasks.clear();

                log.info("-------------------------------------------------");
                log.info("Обрабатываем асины, полеченные по ссылке пользователя");
                reqTasks.addAll(convertToTasks(Amazon.parseCategory(pages), "https://www.amazon.com/dp/"));
                pages.clear();
            }


            // Выкачиваем первую волну
            reqTasks.addAll(convertToTasks(Handler.readListOfAsin(params.getPathToListing()), "https://www.amazon.com/dp/", ReqTaskType.SEARCH_ITEM));

            List<RequestTask> sdf = RequestManager.execute(reqTasks, DEBUG_MOD);
            reqTasks.clear();

            // TODO Написать новый парсер который выберит всё с характеристик и вендора
            Amazon.parseShortItems(sdf);
            sdf.clear();


            System.exit(1);

            if (!params.getPathToListing().isEmpty())
                reqTasks.addAll(convertToTasks(Handler.readListOfAsin(params.getPathToListing()), "https://www.amazon.com/dp/"));

            if (reqTasks.size() == 0)
                throw new Exception("Не было получено ни одного ASIN для обработки");

            log.info("Execute the request to load the listing");
            List<RequestTask> amazonItems = RequestManager.execute(reqTasks, DEBUG_MOD);
            reqTasks.clear();

            result = Amazon.parseItems(amazonItems);
            amazonItems.clear();

            log.info("-------------------------------------------------");
            log.info("Filter the results");
            filterResult();
            log.info("The filtering is complete, leaving " + result.size() + " positions");

//            log.info("-------------------------------------------------");
//            log.info("Create and download search queries ( Ebay )");
//            for (AmazonItem item : result) {
//                Integer num = 0;
//                for (String req : item.getSearchReq()) {
//                    RequestTask task = new RequestTask(item.getAsin() + ":" + String.valueOf(num++));
//                    task.setUrl("https://www.ebay.com/sch/i.html?_from=R40&_nkw=" + URLEncoder.encode(req, "UTF-8") + "&_sacat=0&LH_TitleDesc=0&_fcid=1&_sop=15&_dmd=1&LH_BIN=1&LH_ItemCondition=3&LH_RPA=1&_stpos=07064&rt=nc&LH_PrefLoc=3&LH_ItemCondition=3");
//                    task.setType(ReqTaskType.EBAY_CATEGORY);
//
//                    reqTasks.add(task);
//                }
//            }
//            List<RequestTask> ebaySearchResult = RequestManager.execute(reqTasks, DEBUG_MOD);
//            reqTasks.clear();
//            List<Search> ebaySearch = Ebay.parseSearchReq(ebaySearchResult);
//            ebaySearchResult.clear();
//
//            log.info("-------------------------------------------------");
//            log.info("Download items received after the search ( Ebay )");
//            reqTasks = convertToTasks(getAsinsFromSearchResult(ebaySearch), "https://www.ebay.com/itm/");
//
//            List<RequestTask> allEbayItems = RequestManager.execute(reqTasks, DEBUG_MOD);
//            reqTasks.clear();
//            List<EbayItem> ebaySearchItems = Ebay.parseItems(allEbayItems);
//            allEbayItems.clear();
//
//            log.info("-------------------------------------------------");
//            log.info("Merge the result");
//            for (AmazonItem item : result) {
//                if (item.getSearchReq().size() == 0)
//                    continue;
//
//                ArrayList<EbayItem> searchInfo = new ArrayList<>();
//
//                List<Search> search = ebaySearch.stream()
//                        .filter(x -> x.getRelatedAsin().equals(item.getAsin()))
//                        .collect(Collectors.toList());
//
//                HashSet<String> asins = new HashSet<>();
//                search.forEach(s -> asins.addAll(s.getAsins()));
//
//                int asinCount = 0;
//                for (String asin : asins) {
//                    if (asinCount++ > 2) break;
//                    Optional<EbayItem> first = ebaySearchItems.stream()
//                            .filter(x -> x.getItemNumber().equals(asin))
//                            .findFirst();
//
//                    if (first.isPresent()) {
//                        EbayItem info = new EbayItem();
//                        info.setItemNumber(asin);
//                        info.setPrice(first.get().getPrice());
//                        info.setSeller(first.get().getSeller());
//                        info.setShipping(first.get().getShipping());
//                        info.setPriceShipping(first.get().getPriceShipping());
//
//                        searchInfo.add(info);
//                    }
//                }
//
//                item.setEbayItems(searchInfo);
//            }
//            ebaySearchItems.clear();
//            ebaySearch.clear();


            log.info("-------------------------------------------------");
            log.info("Create and download search queries ( Amazon )");

            for (AmazonItem item : result) {
                Integer num = 0;
                for (String req : item.getSearchReq()) {
                    RequestTask task = new RequestTask(item.getAsin() + ":" + String.valueOf(num++));
                    task.setUrl("https://www.amazon.com/s?field-keywords=" + URLEncoder.encode(req, "UTF-8"));
                    task.setType(ReqTaskType.SEARCH);

                    reqTasks.add(task);
                }
            }

            List<RequestTask> amazonSearchResult = RequestManager.execute(reqTasks, DEBUG_MOD);
            reqTasks.clear();
            List<Search> searchResult = Amazon.parseSearchReq(amazonSearchResult);
            amazonSearchResult.clear();

            // --------------------- НОВЫЙ БЛОК ---------------------
            /*
                1. Сформировать новый лист тасков
                   для каждого relatedAsin не больше 5 asins
                   не добавлять асины которые == relatedAsin
                   каждый добавленый asin удалять из списка
             */

            HashSet<String> uniqueAsins = new HashSet<>();
            for (AmazonItem item : result) {
                if (item.getSearchReq().size() == 0 || item.getSearchInfo().size() >= 3)
                    continue;

                Optional<Search> any = searchResult.stream().filter(srch -> srch.getRelatedAsin().equals(item.getAsin())).findAny();
                if (any.isPresent()) {
                    Search search = any.get();
                    Iterator<String> iterator = search.getAsins().iterator();

                    int maxCount = 0;
                    while (iterator.hasNext() && maxCount++ < 5) {
                        String asin = iterator.next();
                        if (search.getRelatedAsin().equals(asin)) {
                            iterator.remove();
                            continue;
                        }

                        uniqueAsins.add(asin);
                    }
                }
            }

            // Выкачиваем первую волну
            reqTasks = convertToTasks(new ArrayList<>(uniqueAsins), "https://www.amazon.com/dp/", ReqTaskType.SEARCH_ITEM);
            uniqueAsins.clear();

            List<RequestTask> searchAmazonItems = RequestManager.execute(reqTasks, DEBUG_MOD);
            reqTasks.clear();

            // TODO Написать новый парсер который выберит всё с характеристик и вендора
            List<ItemShortInfo> searchItems = Amazon.parseShortItems(searchAmazonItems);
            searchAmazonItems.clear();

            // Заполняем результат
            for (AmazonItem item : result) {
                if (item.getSearchReq().size() == 0 || item.getSearchInfo().size() >= 3)
                    continue;

                Optional<Search> any = searchResult.stream().filter(srch -> srch.getRelatedAsin().equals(item.getAsin())).findAny();
                if (any.isPresent()) {
                    Search search = any.get();
                    Iterator<String> iterator = search.getAsins().iterator();

                    while (iterator.hasNext()) {
                        String asin = iterator.next();



//                        Optional<AmazonItem> anySItem = searchItems.stream().filter(sIt -> sIt.getAsin().equals(asin)).findAny();
//                        if (anySItem.isPresent()) {
//                            iterator.remove();
//                            AmazonItem shortInfo = anySItem.get();
//
//                            // TODO Здесь нужен иф в котором будут сравниватся два итема
//                            if ()
//
//
//                            ItemShortInfo info = new ItemShortInfo();
//                            info.setAsin(asin);
//                            info.setAvailability(shortInfo.getAvailability());
//                            info.setIsNew(shortInfo.getNew());
//
//                            item.getSearchInfo().add(info);
//                        }
                    }
                }
            }

            System.out.println();


            /*log.info("-------------------------------------------------");
            log.info("Download items received after the search ( Amazon )");
            reqTasks = convertToTasks(getAsinsFromSearchResult(searchResult), "https://www.amazon.com/dp/", ReqTaskType.SEARCH_ITEM);

            List<RequestTask> searchAmazonItems = RequestManager.execute(reqTasks, DEBUG_MOD);
            reqTasks.clear();

            List<AmazonItem> searchItems = Amazon.parseItems(searchAmazonItems);
            searchAmazonItems.clear();

            log.info("-------------------------------------------------");
            log.info("Merge the result");
            for (AmazonItem item : result) {
                if (item.getSearchReq().size() == 0)
                    continue;

                ArrayList<ItemShortInfo> searchInfo = new ArrayList<>();

                List<Search> search = searchResult.stream()
                        .filter(x -> x.getRelatedAsin().equals(item.getAsin()))
                        .collect(Collectors.toList());

                HashSet<String> asins = new HashSet<>();
                search.forEach(s -> asins.addAll(s.getAsins()));

                int asinCount = 0;
                for (String asin : asins) {
                    if (asinCount++ > 2) break;
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
            searchResult.clear();*/

            log.info("Form the list of requests for the loading of offers");
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

            List<RequestTask> allOffers = RequestManager.execute(reqTasks, DEBUG_MOD);
            reqTasks.clear();

            List<ItemOffer> offers = Amazon.parseOffers(allOffers);
            allOffers.clear();

            log.info("-------------------------------------------------");
            log.info("Fill out the promo");

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

            log.info("-------------------------------------------------");
            log.info("FULL TIME OF PERFORMANCE: " + (new Date().getTime() - time) + " ms");
            log.info("-------------------------------------------------");

            DBHandler.close();
        } catch (Exception e) {
            log.info("-------------------------------------------------");
            log.log(Level.SEVERE, "Ошибка во время выполнения таска, закрываем такс");
            log.log(Level.SEVERE, "Exception: " + e.getMessage());

            e.printStackTrace();
        } finally {
            status = 100;
        }

    }

    private String updateZipCode(String zip) {

        String result = "";
        if (zip.isEmpty()) zip = "07064";
        String address = "https://www.amazon.com/gp/delivery/ajax/address-change.html";

        try {
            Map<String, String> cookies = new HashMap<>();
            cookies.put("session-id", "131-6060488-5218335");
            cookies.put("session-id-time", "2082787201l");
            cookies.put("ubid-main", "134-8378907-0944350");

            Connection.Response res = Jsoup.connect(address)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0")
                    .ignoreContentType(true)
                    .cookies(cookies)
                    .data(
                            "actionSource", "glow",
                            "deviceType", "web",
                            "locationType", "LOCATION_INPUT",
                            "pageType", "Gateway",
                            "zipCode", zip)
                    .method(Connection.Method.POST)
                    .execute();

            String sessionId = res.cookie("session-id");
            String sessionTime = res.cookie("session-id-time");
            String ubid = res.cookie("ubid-main");

            result = String.format("session-id=%s; session-id-time=%s; ubid-main=%s", sessionId, sessionTime, ubid);
        } catch (Exception e) {
            log.info("-------------------------------------------------");
            log.log(Level.SEVERE, "Не удалось обновить zip code");
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        }

        return result;
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
            task.setUrl(url + "&page=" + i);
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

    private HashSet<String> getAsinsFromSearchResult(List<Search> searchResult) {

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

        return asins;
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

    private List<RequestTask> convertToTasks(Collection<String> asins, String prefix, ReqTaskType type) {
        List<RequestTask> result = new ArrayList<>();
        for (String asin : asins) {
            RequestTask task = new RequestTask(asin);
            task.setUrl(prefix + asin);
            task.setType(type);

            result.add(task);
        }

        return result;
    }

    private List<RequestTask> convertToTasks(Collection<String> asins, String prefix) {
        List<RequestTask> result = new ArrayList<>();
        for (String asin : asins) {
            RequestTask task = new RequestTask(asin);
            task.setUrl(prefix + asin);
            task.setType(prefix.contains("ebay") ? ReqTaskType.EBAY_ITEM : ReqTaskType.ITEM);

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
