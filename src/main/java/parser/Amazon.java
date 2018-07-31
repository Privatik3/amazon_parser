package parser;

import manager.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Amazon {

    public static ArrayList<AmazonItem> parseItems(ArrayList<Task> tasks) {

        ArrayList<AmazonItem> result = new ArrayList<>();
        for (Task task : tasks) {
            AmazonItem item = new AmazonItem();
            item.setAsin(task.getId());

            Document doc = Jsoup.parse(task.getHtml());
            // Здесь гавнарит Александр






//            Boolean availability = false;
//            try {
//                Elements inStockStatus = doc.select("div#availability");
//                availability = inStockStatus.size() > 0 && !inStockStatus.text().contains("unavailable");
//            } catch (Exception ignored) {}
//            item.setAvailability(availability);
//
//            Boolean promo = false;
//            try {
//                Elements promoStatus = doc.select("div#pe-bb-header");
//                promo = promoStatus.size() > 0 && promoStatus.text().contains("Prime");
//            } catch (Exception ignored) {}
//            item.setPromoOffer(promo);
//
//            String vendor = "";
//            try {
//                vendor = doc.select("a#bylineInfo").text();
//            } catch (Exception ignored) {}
//            item.setVendor(vendor);

//            String name = "";
//            try {
//                name = doc.select("span#productTitle").text();
//            } catch (Exception ignored) {}
//            item.setProductName(name);

//            //TODO: Багает парс имени продавца, не всегда есть а тег.
//            String sellerName = "";
//            try {
//                sellerName = doc.select("#merchant-info a").get(0).text();
//            } catch (Exception ignored) {}
//            item.setBuyBoxSeller(sellerName);

//            //TODO: Багает парс цены продавца, не всегда есть span тег.
//            String price = "";
//            try {
//                price = doc.select("#priceblock_ourprice").text();
//            } catch (Exception ignored) {}
//            item.setBuyBoxPrice(price);

            String Shipping = "";
            try {
                Shipping = doc.select("#olp_feature_div span span.a-color-secondary").text();
            } catch (Exception ignored) {}
            item.setBuyBoxShipping(Shipping);









//            Element h1 = doc.select("h1").get(2);
//            item.setAvailability(h1.equals("sdfsdf"));

            // Здесь уже норм код
            result.add(item);
        }

        return result;
    }
}
