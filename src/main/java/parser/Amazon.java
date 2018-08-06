package parser;

import manager.RequestTask;
import manager.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Amazon {

    private static Logger log = Logger.getLogger(Task.class.getName());

    public static List<AmazonItem> parseItems(List<RequestTask> tasks) {

        log.info("-------------------------------------------------");
        log.info("Начинаем обработку листингов");

        long start = new Date().getTime();

        ArrayList<AmazonItem> result = new ArrayList<>();
        for (RequestTask task : tasks) {
            AmazonItem item = new AmazonItem();
            item.setAsin(task.getId());

            Document doc = Jsoup.parse(task.getHtml());
            // Здесь гавнарит Александр

            Boolean availability = false;
            try {
                Elements inStockStatus = doc.select("div#availability");
                availability = inStockStatus.size() > 0 && !inStockStatus.text().contains("unavailable");
            } catch (Exception ignored) {}
            item.setAvailability(availability);

            Boolean promo = false;
            try {
                Elements promoStatus = doc.select("div#pe-bb-header");
                promo = promoStatus.size() > 0 && promoStatus.text().contains("Prime");
            } catch (Exception ignored) {}
            item.setPromoOffer(promo);

            String vendor = "";
            try {
                vendor = doc.select("a#bylineInfo").text();
            } catch (Exception ignored) {}
            item.setVendor(vendor.trim());

            String name = "";
            try {
                name = doc.select("span#productTitle").text();
            } catch (Exception ignored) {}
            item.setProductName(name);

            //TODO: Багает парс имени продавца, не всегда есть а тег.
            String sellerName = "";
            try {
                sellerName = doc.select("#merchant-info a").get(0).text();
            } catch (Exception ignored) {}
            item.setBuyBoxSeller(sellerName);

            //TODO: Багает парс цены продавца, не всегда есть span тег.
            Double price = 0.00;
            try {
                String priceOne = doc.select("#priceblock_ourprice").text();
                if (priceOne.length() > 2) {
                    priceOne = priceOne.substring(1);
                    priceOne = priceOne.split(" ")[0];
                    price = Double.parseDouble(priceOne);
                }
                if (priceOne.length() < 1) {
                    priceOne = doc.select("#olp_feature_div").text();
                    priceOne = priceOne.substring(priceOne.indexOf("from") + 6, priceOne.indexOf(".")+3);
                    priceOne = priceOne.replaceAll(",", "");
                    price =  Double.parseDouble(priceOne);
                }
            } catch (Exception ignored) {}
            item.setBuyBoxPrice(price);

            String Shipping = "";
            try {
                Shipping = doc.select("#olp_feature_div span span.a-color-secondary").text();
                if (Shipping.length() < 1) {
                    Shipping = doc.select("#olp_feature_div").text();
                    Shipping = Shipping.substring(Shipping.indexOf("+")+1);
                }
            } catch (Exception ignored) {}
            item.setBuyBoxShipping(Shipping);



//            try {
//               String shipingConverter = item.getBuyBoxShipping();
//               if (shipingConverter.contains("")) {
//
//
//
//               }
//            }catch (Exception ignored) {}

            String brand = "";
            try {
                Elements  brandEl = doc.select("#product-specification-table tr");
                for( Element el : brandEl ) {
                    if (el.text().contains("Brand")) {
                        brand = el.select("td").text();
                    }
                }
            } catch (Exception ignored) {}
            item.setBrand(brand.trim());

            String partNumber = "";
            try {
                Elements  partNumberEl = doc.select("th");
                for( Element el : partNumberEl ) {
                    if (el.text().contains("Part Number")) {
                        partNumber = el.parent().select("td").text();
                    }
                }
            } catch (Exception ignored) {}
            item.setPartNumber(partNumber.trim());

            String itemModelNumber = "";
            try {
                Elements  itemModelNumberEl = doc.select("th");
                for( Element el : itemModelNumberEl ) {
                    if (el.text().contains("Item model number")) {
                        itemModelNumber = el.parent().select("td").text();
                    }
                }
                if (itemModelNumber.length() < 1) {
                    itemModelNumberEl = doc.select("li");
                    for( Element el : itemModelNumberEl ) {
                        if (el.text().contains("Item model number")) {
                            itemModelNumber = el.text();
                            itemModelNumber = itemModelNumber.split(":")[1];
                        }
                    }
                }
            } catch (Exception ignored) {}
            item.setItemModelNumber(itemModelNumber.trim());


            String asinDomin = "";
            try {
                Elements  asinDominEl = doc.select("th");
                for( Element el : asinDominEl ) {
                    if (el.text().contains("ASIN")) {
                        asinDomin = el.parent().select("td").text();
                    }
                }
                if (asinDomin.length() < 1) {
                    asinDominEl = doc.select("li");
                    for( Element el : asinDominEl ) {
                        if (el.text().contains("ASIN")) {
                            asinDomin = el.text();
                            asinDomin = asinDomin.split(":")[1];
                        }
                    }
                }
                if (asinDomin.length() < 1) {
                    asinDominEl = doc.select("td");
                    for( Element el : asinDominEl ) {
                        if (el.text().contains("ASIN")) {
                            asinDomin = el.parent().select("td.value").text();
                        }
                    }
                }
            } catch (Exception ignored) {}
            item.setAsinDomin(asinDomin);

            String textRating = "";
            Double rating = -1.0;
            try {
                Elements select = doc.select("#acrPopover span");
                if (select.size() > 1) {
                    textRating = select.get(1).text();
                    textRating = textRating.substring(0, 3);
                }
                textRating = getRating(doc, textRating, "#productDetails_db_sections tr td");
                textRating = getRating(doc, textRating, "#productDetails_detailBullets_sections1 td");
                if (textRating.length() < 1) {
                    select = doc.select("td");
                    for( Element el : select ) {
                        if (el.text().contains("Customer Reviews")) {
                            textRating = el.parent().select("td.value span").text();
                            textRating = textRating.split(" out")[0];
                        }
                    }
                }
                rating =  Double.parseDouble(textRating);
            } catch (Exception ignored) {}
            item.setRating(rating);

            //TODO Остановка проверки

            String quantity = "0";
            try {
                quantity = doc.select("#acrCustomerReviewText").text();
                if (!quantity.equals("")) {
                    quantity = quantity.split(" customer")[0];
                }
                if (quantity.equals("")) {
                    quantity = "0";
                }
            } catch (Exception ignored) {}
            item.setQuantity(quantity);

            String bSRDouble = "";
            Integer bSR = 0;
            try {
                Elements bSREl = doc.select("th");
                for (Element el : bSREl) {
                    if (el.text().contains("Best Sellers Rank")) {
                        bSRDouble = el.parent().select("td").text();
                        bSRDouble = bSRDouble.split(" in")[0];
                        bSRDouble = bSRDouble.split("#")[1];
                        bSRDouble = bSRDouble.replace(",", "");
                    }
                }
                if (bSRDouble.length() < 1) {
                    bSRDouble = getBSR(doc , "#SalesRank");
                }
                bSR =  Integer.parseInt(bSRDouble);
            } catch (Exception ignored) {}
            item.setbSR(bSR);

            String bSRCategory = "";
            try {
                Elements  bSRCategoryEl = doc.select("th");

                for( Element el : bSRCategoryEl ) {
                    if (el.text().contains("Best Sellers Rank")) {
                        bSRCategory = el.parent().select("td").text();
                        bSRCategory = bSRCategory.split("in ")[1];
                        bSRCategory = bSRCategory.split("\\(S")[0];
                    }
                }
                if (bSRCategory.length() < 1) {
                    bSRCategory = getbSRCategory(doc , "#SalesRank");
                }
                bSRCategory = bSRCategory.split(">")[0];
            } catch (Exception ignored) {}
            item.setbSRCategory(bSRCategory);

            Date dateCreation = new Date();
            try {
                String dateFirstAvailable = "";
                Elements dateFirstAvailableEl = doc.select("th");
                for (Element el : dateFirstAvailableEl) {
                    if (el.text().contains("Date first") || el.text().contains("Date First")) {
                        dateFirstAvailable = el.parent().select("td").text();
                        break;
                    }
                }
                String[] split = dateFirstAvailable.split(" ");
                String monthData = split[0];
                String day = dateFirstAvailable.split(" ")[1];
                day = day.replace(",", "");
                String year = dateFirstAvailable.split(" ")[2];
                int month = new SimpleDateFormat("MMMM", Locale.US).parse(monthData).getMonth() + 1;
                dateFirstAvailable = month + "/" + day + "/" + year;
                dateCreation = new SimpleDateFormat("MM/dd/yyyy").parse(dateFirstAvailable);
            } catch (Exception ignored) {}
            item.setDateFirstAvailable(dateCreation);

            String newHref = "";
            try {
                newHref = "https://www.amazon.com" + doc.select("#olp_feature_div a").attr("href");
            } catch (Exception ignored) {}
            item.setNewHref(newHref);

            HashSet<String> searchReq = new HashSet<>();
            try {
                if (!vendor.isEmpty() && !partNumber.isEmpty())
                    searchReq.add(String.format("%s+%s", item.getVendor(), item.getPartNumber()));

                if (!vendor.isEmpty() && !itemModelNumber.isEmpty())
                    searchReq.add(String.format("%s+%s", item.getVendor(), item.getItemModelNumber()));

                if (!brand.isEmpty() && !partNumber.isEmpty())
                    searchReq.add(String.format("%s+%s", item.getBrand(), item.getPartNumber()));

                if (!brand.isEmpty() && !itemModelNumber.isEmpty())
                    searchReq.add(String.format("%s+%s", item.getBrand(), item.getItemModelNumber()));
            } catch (Exception ignored) {}
            item.setSearchReq(searchReq);

            // Здесь уже норм код
            result.add(item);
        }

        log.info("Время затраченое на обработку: " + (new Date().getTime() - start) + " ms");
        return result;
    }

    private static String getbSRCategory(Document doc, String bSRSelect) {
        String bSRCategory;
        bSRCategory = doc.select(bSRSelect).text();
        bSRCategory = bSRCategory.split("in ")[1];
        bSRCategory = bSRCategory.split("\\(S")[0];
        return bSRCategory;
    }

    private static String getBSR(Document doc, String bSRSelect) {
        String bSRDouble;
        bSRDouble = doc.select(bSRSelect).text();
        bSRDouble = bSRDouble.split(" in")[0];
        bSRDouble = bSRDouble.split("#")[1];
        bSRDouble = bSRDouble.replace(",", "");
        return bSRDouble;
    }

    private static String getRating(Document doc, String textRating, String select) {
        if (textRating.length() < 1) {
            Elements ratingEl = doc.select(select);
            for (Element el : ratingEl) {
                if (el.text().contains("stars")) {
                    textRating = el.text().split("item ")[1];
                    textRating = textRating.split(" out")[0];
                }
            }
        }
        return textRating;
    }

    public static List<ItemOffer> parseOffers(List<RequestTask> tasks) {

        log.info("-------------------------------------------------");
        log.info("Начинаем обработку оферов");

        long start = new Date().getTime();

        ArrayList<ItemOffer> result = new ArrayList<>();
        for (RequestTask task : tasks) {
            ItemOffer item = new ItemOffer();
            item.setAsin(task.getId());

            Document doc = Jsoup.parse(task.getHtml());
            // Здесь гавнарит Александр

            //TODO Парсинг селекторов NEW
            List<Offer> priceNew = new ArrayList<>();
            try {
                Elements priceNewEl = doc.select("#olpOfferList p.olpShippingInfo");

                for (int i = 0; i < (priceNewEl.size() > 5 ? 5 : priceNewEl.size()); i++) {
                    Offer offer = new Offer();
                    Element el = priceNewEl.get(i);

                    offer.setShipingInfo(el.text());
                    priceNewEl = el.parent().select("span");

                    offer.setPrice(priceNewEl.get(0).text());
                    String SellerNew = priceNewEl.parents().parents().select(".olpSellerColumn h3").text();

                    if (SellerNew.length() < 1)
                        SellerNew = priceNewEl.parents().parents().select(".olpSellerColumn h3 img").attr("alt");

                    offer.setSeller(SellerNew);

                    priceNew.add(offer);
                }
            } catch (Exception ignored) {}
            item.setOffers(priceNew);

            // Здесь уже норм код
            result.add(item);
        }

        log.info("Время затраченое на обработку: " + (new Date().getTime() - start) + " ms");

        return result;
    }

    public static List<AmazonSearch> parseSearchReq(List<RequestTask> tasks) {

        log.info("-------------------------------------------------");
        log.info("Начинаем обработку результатов поиска");

        long start = new Date().getTime();

        ArrayList<AmazonSearch> result = new ArrayList<>();
        for (RequestTask task : tasks) {
            AmazonSearch item = new AmazonSearch();
            item.setRelatedAsin(task.getId().substring(0, task.getId().length() - 2));

            Document doc = Jsoup.parse(task.getHtml());
            // Здесь гавнарит Александр

            List<String> asins = new ArrayList<>();
            try {
                Elements asinNewEl = doc.select("li[id^='result']");
                for (int i = 0; i < (asinNewEl.size() > 3 ? 3 : asinNewEl.size()); i++) {
                    Element el = asinNewEl.get(i);
                    asins.add(el.attr("data-asin"));
                }
            } catch (Exception ignored) {}
            item.setAsins(asins);

            // Здесь уже норм код
            result.add(item);
        }

        log.info("Время затраченое на обработку: " + (new Date().getTime() - start) + " ms");

        return result;
    }


}
