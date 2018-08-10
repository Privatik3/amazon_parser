package parser;

import manager.RequestTask;
import manager.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

            Double price = 0.00;
            String priceText = "";
            try {
                priceText = doc.select("#prcIsum").attr("content");
                price = Double.parseDouble(priceText);
            } catch (Exception ignored) {}
            item.setPrice(price);

            String seller = "";
            try {
                seller = doc.select("#mbgLink span").text();
            } catch (Exception ignored) {}
            item.setSeller(seller);

            String shipping = "";
            try {
                shipping = doc.select("#fshippingCost").text();
            } catch (Exception ignored) {}
            item.setShipping(shipping);

            // Здесь уже норм код
            result.add(item);
        }

        log.info("Время затраченое на обработку: " + (new Date().getTime() - start) + " ms");
        return result;
    }

    public static List<Search> parseSearchReq(List<RequestTask> tasks) {

        log.info("-------------------------------------------------");
        log.info("Начинаем обработку результатов поиска");

        long start = new Date().getTime();

        ArrayList<Search> result = new ArrayList<>();
        for (RequestTask task : tasks) {
            Search item = new Search();
            item.setRelatedAsin(task.getId().substring(0, task.getId().length() - 2));

            Document doc = Jsoup.parse(task.getHtml());
            // Здесь гавнарит Александр

            List<String> asins = new ArrayList<>();
            try {

                asins.add("281290254100");
//                Elements asinNewEl = doc.select("li[id^='result']");
//                for (int i = 0; i < (asinNewEl.size() > 3 ? 3 : asinNewEl.size()); i++) {
//                    Element el = asinNewEl.get(i);
//                    asins.add(el.attr("data-asin"));
//                }
            } catch (Exception ignored) { }
            item.setAsins(asins);

            // Здесь уже норм код
            result.add(item);
        }

        log.info("Время затраченое на обработку: " + (new Date().getTime() - start) + " ms");

        return result;
    }
}
