package parser;

import manager.RequestTask;
import manager.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Amazon {

    public static List<AmazonItem> parseItems(List<RequestTask> tasks) {

        ArrayList<AmazonItem> result = new ArrayList<>();
        for (RequestTask task : tasks) {
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
//            Double price = 0.00;
//            try {
//              String pririceOne = doc.select("#priceblock_ourprice").text();
//                price =  Double.parseDouble(pririceOne.split("\\$")[1]);
//            } catch (Exception ignored) {}
//            item.setBuyBoxPrice(price);

//            String Shipping = "";
//            try {
//                Shipping = doc.select("#olp_feature_div span span.a-color-secondary").text();
//            } catch (Exception ignored) {}
//            item.setBuyBoxShipping(Shipping);

//            String brand = "";
//            try {
//                Elements  brandEl = doc.select("#product-specification-table tr");
//                for( Element el : brandEl ) {
//                    if (el.text().contains("Brand")) {
//                       brand = el.select("td").text();
//                    }
//                }
//            } catch (Exception ignored) {}
//            item.setBrand(brand);

//            String partNumber = "";
//            try {
//                Elements  partNumberEl = doc.select("th");
//                for( Element el : partNumberEl ) {
//                    if (el.text().contains("Part Number")) {
//                        partNumber = el.parent().select("td").text();
//                    }
//                }
//            } catch (Exception ignored) {}
//            item.setPartNumber(partNumber);

//            String itemModelNumber = "";
//            try {
//                Elements  itemModelNumberEl = doc.select("th");
//                for( Element el : itemModelNumberEl ) {
//                    if (el.text().contains("Item model number")) {
//                        itemModelNumber = el.parent().select("td").text();
//                    }
//                }
//                if (itemModelNumber.length() < 1) {
//                    itemModelNumberEl = doc.select("li");
//                    for( Element el : itemModelNumberEl ) {
//                        if (el.text().contains("Item model number")) {
//                            itemModelNumber = el.text();
//                            itemModelNumber = itemModelNumber.split(":")[1];
//                        }
//                    }
//                }
//            } catch (Exception ignored) {}
//            item.setItemModelNumber(itemModelNumber);

//            String asinDomin = "";
//            try {
//                Elements  asinDominEl = doc.select("th");
//                for( Element el : asinDominEl ) {
//                    if (el.text().contains("ASIN")) {
//                        asinDomin = el.parent().select("td").text();
//                    }
//                }
//                if (asinDomin.length() < 1) {
//                    asinDominEl = doc.select("li");
//                    for( Element el : asinDominEl ) {
//                        if (el.text().contains("ASIN")) {
//                            asinDomin = el.text();
//                            asinDomin = asinDomin.split(":")[1];
//                        }
//                    }
//                }
//            } catch (Exception ignored) {}
//            item.setAsinDomin(asinDomin);

//            String textRating = "";
//            Double rating = -1.0;
//            try {
//                Elements select = doc.select("#acrPopover span");
//                if (select.size() > 1) {
//                    textRating = select.get(1).text();
//                    textRating = textRating.substring(0, 3);
//                }
//                textRating = getRating(doc, textRating, "#productDetails_db_sections tr td");
//                textRating = getRating(doc, textRating, "#productDetails_detailBullets_sections1 td");
//                rating =  Double.parseDouble(textRating);
//            } catch (Exception ignored) {}
//            item.setRating(rating);


//            String quantity = "0";
//            try {
//                quantity = doc.select("#acrCustomerReviewText").text();
//                if (!quantity.equals("")) {
//                    quantity = quantity.split(" customer")[0];
//                }
//                if (quantity.equals("")) {
//                    quantity = "0";
//                }
//            } catch (Exception ignored) {}
//            item.setQuantity(quantity);

            String bSRDouble = "";
            Double bSR = 0.0;
            try {
                Elements  bSREl = doc.select("th");

                for( Element el : bSREl ) {
                    if (el.text().contains("Best Sellers Rank")) {
                        bSRDouble = el.parent().select("td").text();
                        bSRDouble = bSRDouble.split(" in")[0];
                        bSRDouble = bSRDouble.split("#")[1];
                        bSRDouble = bSRDouble.replace(',', '.');
                    }
                }

                if (bSRDouble.length() < 1) {
                    bSRDouble = getBSR(doc , "#SalesRank");
                }
                bSR =  Double.parseDouble(bSRDouble);
            } catch (Exception ignored) {}
            item.setbSR(bSR);










//            Element h1 = doc.select("h1").get(2);
//            item.setAvailability(h1.equals("sdfsdf"));

            // Здесь уже норм код
            result.add(item);
        }

        return result;
    }

    private static String getBSR(Document doc, String bSRSelect) {
        String bSRDouble;
        bSRDouble = doc.select(bSRSelect).text();
        bSRDouble = bSRDouble.split(" in")[0];
        bSRDouble = bSRDouble.split("#")[1];
        bSRDouble = bSRDouble.replace(',', '.');
        return bSRDouble;
    }

    private static String getRating(Document doc, String textRating, String select) {
        if (textRating.length() < 1) {
            Elements ratingEl = doc.select(select);
            for( Element el : ratingEl ) {
                if (el.text().contains("stars")) {
                    textRating = el.text().split("item ")[1];
                    textRating = textRating.split(" out")[0];
                }
            }
        }
        return textRating;
    }
}
