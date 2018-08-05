package manager;

import db.DBHandler;
import excel.Handler;
import face.InterfaceParams;
import parser.Amazon;
import parser.AmazonItem;
import utility.RequestManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
            RequestManager.execute(reqTasks);


            /*List<Filter> filters = params.getFilters();
            filters.stream().filter()

            for (AmazonItem item : amazonItems) {

            }*/

            result =  Amazon.parseItems(DBHandler.selectAllItems());
            status = 100;

            log.info("-------------------------------------------------");
            log.info("ПОЛНОЕ ВРЕМЯ ВЫПОЛНЕНИЯ: " + (new Date().getTime() - time) + " ms");
            log.info("-------------------------------------------------");

            DBHandler.close();
        } catch (Exception e) {
            e.printStackTrace();
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
