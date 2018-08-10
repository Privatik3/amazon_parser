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

            Double price = 0.00;
            String priceText = "";
            try {
                priceText = doc.select("#prcIsum").attr("content");
                price = Double.parseDouble(priceText);
            } catch (Exception ignored) {
            }
            item.setPrice(price);

            String seller = "";
            try {
                seller = doc.select("#mbgLink span").text();
            } catch (Exception ignored) {
            }
            item.setSeller(seller);

            String shipping = "";
            try {
                shipping = doc.select("#fshippingCost").text();
            } catch (Exception ignored) {
            }
            item.setItemNumber(shipping);

            // Здесь уже норм код
            result.add(item);
        }

        log.info("Время затраченое на обработку: " + (new Date().getTime() - start) + " ms");
        return result;
    }
}
