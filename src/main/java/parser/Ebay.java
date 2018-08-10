package parser;

import manager.RequestTask;
import manager.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Ebay {

    private static Logger log = Logger.getLogger(Task.class.getName());

    public static List<EbayItem> parseItems(List<RequestTask> tasks) {

        log.info("-------------------------------------------------");
        log.info("Начинаем обработку ebay листингов");

        long start = new Date().getTime();

        ArrayList<EbayItem> result = new ArrayList<>();
        for (RequestTask task : tasks) {
            EbayItem item = new EbayItem();
            item.setItemNumber(task.getId());

            Document doc = Jsoup.parse(task.getHtml());
            // Начало

            String itemNumber = "";
            try {
                itemNumber = doc.select("#descItemNumber").text();
            } catch (Exception ignored) {
            }
            item.setItemNumber(itemNumber);

            Double price = "";
            try {
                price = doc.select("#prcIsum").attr("content");
            } catch (Exception ignored) {
            }
            item.setItemNumber(price);


            // Здесь уже норм код
            result.add(item);
        }

        log.info("Время затраченое на обработку: " + (new Date().getTime() - start) + " ms");
        return result;
    }
}
