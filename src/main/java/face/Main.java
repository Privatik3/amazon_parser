package face;

import manager.Manager;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

public class Main {

    private static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws ParseException, InterruptedException {

        Logger system = Logger.getLogger("");
        Handler[] handlers = system.getHandlers();
        system.removeHandler(handlers[0]);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return
                        new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()) + " -> " +
                        record.getMessage() + "\r\n";
            }
        });

        system.addHandler(handler);
        system.setUseParentHandlers(false);

//        System.setErr(null);

        log.info("Читаем все параметры с пользовательского интерфейса.");
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
        filters.add(new Filter(FilterType.NONE, 50000, 600000, false));
        filters.add(new Filter(FilterType.UNAVALIABLE, 50000, 600000, false));
        filters.add(new Filter(FilterType.PRIME, 50000, 600000, false));

        parameters.setFilters(filters);

        // ----------------------------------
        filters.add(new Filter(FilterType.RATING, 3.0, 5.0, false));

        long dateFrom = new SimpleDateFormat("MM/dd/yyyy").parse("4/22/2012").getTime();
        long dateTo = new SimpleDateFormat("MM/dd/yyyy").parse("4/22/2018").getTime();
        filters.add(new Filter(FilterType.CREATION_DATE, dateFrom, dateTo, false));


        log.info("Инициализируем таск, получаем таск ID");
        String taskID = Manager.initTask(parameters);
        Manager.process(taskID);

        Integer status = 0;
        while ((status = Manager.getStatus(taskID)) < 100) {
            Thread.sleep(5000);
//            System.out.println(status);
//            System.out.println("-----------------------------");
        }

        Manager.saveResultToFile(taskID);
        Manager.clearTask(taskID);

    }
}
