package manager;

import db.DBHandler;
import excel.Handler;
import face.Filter;
import face.InterfaceParams;
import parser.Amazon;
import parser.AmazonItem;
import parser.AmazonSearch;
import utility.RequestManager;

import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Task extends Thread {

    private static Logger log = Logger.getLogger(Task.class.getName());

    private Integer status = 0;
    private InterfaceParams params;
    private List<AmazonItem> result = new ArrayList<>();

    public Task(InterfaceParams parameters) {
        this.params = parameters;
        this.setDaemon(true);
    }

    @Override
    public void run() {

        try {
            long time = new Date().getTime();

            List<RequestTask> reqTasks = new ArrayList<>();
            if (!params.getUrlListing().isEmpty())
                reqTasks.addAll(queryCategory());

            if (!params.getPathToListing().isEmpty())
                reqTasks.addAll(convertToTasks(Handler.readListOfAsin(params.getPathToListing())));

            log.info("Выполняем запрос на выкачку листинга");
//            RequestManager.execute(reqTasks);
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
                    RequestTask task = new RequestTask(item.getAsin() + ":" + String.valueOf(num));
                    task.setUrl("https://www.amazon.com/s?field-keywords=" + URLEncoder.encode(req, "UTF-8"));
                    task.setType(ReqTaskType.SEARCH);

                    reqTasks.add(task);
                }
            }

//            RequestManager.execute(reqTasks);

            List<AmazonSearch> searchResult = Amazon.parseSearchReq(reqTasks);

            status = 100;

            log.info("-------------------------------------------------");
            log.info("ПОЛНОЕ ВРЕМЯ ВЫПОЛНЕНИЯ: " + (new Date().getTime() - time) + " ms");
            log.info("-------------------------------------------------");

            DBHandler.close();
        } catch (Exception e) {
            log.info("-------------------------------------------------");
            log.log(Level.SEVERE, "Ошибка во время выполнения таска, закрываем такс");
            log.log(Level.SEVERE, "Exception: ", e);
            e.printStackTrace();
        }

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
                        double dateCreation = (double)item.getDateFirstAvailable().getTime();
                        if (dateCreation < filter.getMin()
                                        || dateCreation > filter.getMax())
                            iterator.remove();

                        continue out;
                }
            }
        }
    }

    private Collection<? extends RequestTask> convertToTasks(List<String> asins) {

        log.info("Формирую ссылки на листинги");
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
