package manager;

import excel.Handler;
import face.InterfaceParams;
import parser.Amazon;
import parser.AmazonItem;
import utility.RequestManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Task extends Thread {

    private Integer status = 0;
    private InterfaceParams params;

    public Task(InterfaceParams parameters) {
        this.params = parameters;
        this.setDaemon(true);
    }

    @Override
    public void run() {

        try {
            List<RequestTask> reqTasks = new ArrayList<>();
            if (!params.getUrlListing().isEmpty())
                reqTasks.addAll(queryCategory());

            if (!params.getPathToListing().isEmpty())
                reqTasks.addAll(convertToTasks(Handler.readListOfAsin(params.getPathToListing())));

            List<RequestTask> result = RequestManager.execute(reqTasks);

            List<AmazonItem> amazonItems = Amazon.parseItems(result);
            for (AmazonItem item : amazonItems)
                System.out.println(item);

            status = 100;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Collection<? extends RequestTask> convertToTasks(List<String> asins) {

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
        //TODO Реализовать метод
        return new ArrayList<>();
    }

    public Integer status() {
        return status;
    }

    public List<AmazonItem> getResult() {

        return new ArrayList<>();
    }
}
