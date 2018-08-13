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

            Double priceShipping = 0.00;
            String priceShippingText = "";
            try {
                priceShippingText = item.getShipping();
                if (!priceShippingText.contains("FREE")) {
                    priceShippingText = priceShippingText.substring(1);
                    priceShipping = item.getPrice() + Double.parseDouble(priceShippingText);
                } else {priceShipping = item.getPrice();}
            } catch (Exception ignored) {}
            item.setPriceShipping(priceShipping);

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

//                asins.add("281290254100");
                String check = "";
                try {
                    check = doc.select("h1.srp-controls__count-heading").text();
                    check = check.split(" r")[0];
                    Integer checkSize = Integer.parseInt(check);
                    for ( Integer i = 0 ; i < (checkSize > 3 ? 3 : checkSize); i++) {
                        check = doc.select("li.s-item .s-item__image a").get(i).attr("href");
                        String s = check.split("\\?")[0];
                        String asin = s.substring(s.lastIndexOf("/") + 1);

                        if(!asin.isEmpty())
                            asins.add(asin);
                    }
                } catch (Exception ignored) {
                }


            } catch (Exception ignored) { }
            item.setAsins(asins);

            // Здесь уже норм код
            result.add(item);
        }

        log.info("Время затраченое на обработку: " + (new Date().getTime() - start) + " ms");

        return result;
    }
}
