package face;

import manager.Manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParseException, InterruptedException {

        InterfaceParams parameters = new InterfaceParams();

        parameters.setUrlListing("");
        parameters.setPathToListing("asin.txt");
        parameters.setPathToBadSellers("");

        // ----------------------------------
        parameters.setCountOfFibers(512);
        parameters.setUsaZipCode("07064");
        parameters.setAddToPrice(6.0);

        // ----------------------------------
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(FilterType.NONE, 20000, 600000, true));
        filters.add(new Filter(FilterType.UNAVALIABLE, 20000, 600000, true));
        filters.add(new Filter(FilterType.PRIME, 20000, 600000, true));

        parameters.setFilters(filters);

        // ----------------------------------
        parameters.setRatingMin(3.0);
        parameters.setRatingMax(5.0);

        parameters.setCreationFrom(new SimpleDateFormat("MM/dd/yyyy").parse("4/22/2012"));
        parameters.setCreationTo(new SimpleDateFormat("MM/dd/yyyy").parse("4/22/2018"));

        String taskID = Manager.initTask(parameters);
        Manager.process(taskID);

        Integer status = 0;
        while ((status = Manager.getStatus(taskID)) < 100) {
            Thread.sleep(1000);
//            System.out.println(status);
//            System.out.println("-----------------------------");
        }

//        Manager.saveResultToFile(taskID);
        Manager.clearTask(taskID);

    }
}
