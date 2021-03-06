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
        log.info("Begin processing the listings");

        long start = new Date().getTime();

        ArrayList<AmazonItem> result = new ArrayList<>();
        for (RequestTask task : tasks) {
            AmazonItem item = new AmazonItem();
            item.setAsin(task.getId());

            Document doc = Jsoup.parse(task.getHtml());
            // Здесь гавнарит Александр

            if (!(doc.select("#dropdown_selected_size_name")).text().contains("Select")) {

                Boolean availability = false;
                try {
                    Elements inStockStatus = doc.select("div#availability");
                    availability = !inStockStatus.text().contains("unavailable");
                } catch (Exception ignored) {
                }
                item.setAvailability(availability);

                Boolean promo = false;
                try {
                    Elements promoStatus = doc.select("div#pe-bb-header");
                    promo = promoStatus.size() > 0 && promoStatus.text().contains("Prime");
                } catch (Exception ignored) {
                }
                item.setPromoOffer(promo);

                String vendor = "";
                try {
                    vendor = doc.select("a#bylineInfo").text();
                    if (vendor.isEmpty()) {
                        vendor = doc.select("a#brand").text();
                    }
                } catch (Exception ignored) {}
                item.setVendor(vendor.trim());

                String name = "";
                try {
                    name = doc.select("span#productTitle").text();
                } catch (Exception ignored) {
                }
                item.setProductName(name);

                String sellerName = "";
                try {
                    Elements sellel = doc.select("#merchant-info a");
                    if (sellel.size() > 0) {
                        sellerName = doc.select("#merchant-info a").get(0).text();
                    }
                    if (sellerName.length() < 1) {
                        sellerName = doc.select(".pa_mbc_on_amazon_offer .mbcMerchantName").text();
                    }
                    if (sellerName.contains("Details")) {
                        sellerName = "";
                    }
                    if (sellerName.contains("easy-to-open packaging")) {
                        sellerName = "Amazon.com";
                    }
                } catch (Exception ignored) {
                }
                item.setBuyBoxSeller(sellerName);

                Double price = 0.00;
                try {
                    String priceOne = doc.select("#newBuyBoxPrice").text();
                    if (priceOne.length() > 2) {
                        priceOne = priceOne.substring(1);
                        priceOne = priceOne.split(" ")[0];
                        price = Double.parseDouble(priceOne);
                    }
                    if (priceOne.length() < 1) {
                        priceOne = doc.select("#price_inside_buybox").text();
                        if (priceOne.length() > 2) {
                            priceOne = priceOne.substring(1);
                            priceOne = priceOne.split(" ")[0];
                            price = Double.parseDouble(priceOne);
                        }
                    }
                    if (priceOne.length() < 1) {
                            priceOne = doc.select("#snsPrice span.a-text-strike").text();
                        if (priceOne.length() > 2) {
                            priceOne = priceOne.substring(1);
                            priceOne = priceOne.split(" ")[0];
                            price = Double.parseDouble(priceOne);
                        }
                    }
                    if (priceOne.length() < 1) {
                        if (doc.select("span#_price").size() > 0) {
                            priceOne = doc.select("span#_price").get(0).text();
                            if (priceOne.length() > 2) {
                                priceOne = priceOne.substring(1);
                                priceOne = priceOne.split(" ")[0];
                                price = Double.parseDouble(priceOne);
                            }
                        }
                    }
                    if (priceOne.length() < 1) {
                        priceOne = doc.select("#priceblock_ourprice").text();
                        if (priceOne.length() > 2) {
                            priceOne = priceOne.substring(1);
                            priceOne = priceOne.split(" ")[0];
                            price = Double.parseDouble(priceOne);
                        }
                    }
                    if (priceOne.length() < 1) {
                        if (doc.select("#snsBuyBoxAccordion span.a-color-price").size() > 0) {
                            priceOne = doc.select("#snsBuyBoxAccordion span.a-color-price").get(0).text();
                            if (priceOne.length() > 2) {
                                priceOne = priceOne.substring(1);
                                priceOne = priceOne.split(" ")[0];
                                price = Double.parseDouble(priceOne);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
                item.setBuyBoxPrice(price);

                String Shipping = "";
                try {
                    Shipping = doc.select("#price-shipping-message").text();
                    if (Shipping.length() > 1) {
                        Shipping = Shipping.split("&")[1];
                        Shipping = Shipping.split("&")[0];
                        Shipping = Shipping.split(".D")[0];
                    }
                    if (Shipping.length() < 1) {
                        Shipping = doc.select("#shippingMessageInsideBuyBox_feature_div").text();
                        if (Shipping.length() > 1) {
                            if (Shipping.contains("+")) {
                                Shipping = Shipping.substring(Shipping.indexOf("+") + 1);
                            }
                        }
                    }
                    if (Shipping.length() < 1) {
                        Shipping = doc.select("#price-shipping-message").text();
                    }
                    if (Shipping.length() < 1) {
                        Shipping = doc.select("#soldByThirdParty span").get(1).text();
                    }
                } catch (Exception ignored) {
                }
                item.setBuyBoxShipping(Shipping);


                //TODO Заменить 5 на значение пользователя
                String shipingConverter = item.getBuyBoxShipping();
                try {
                    if (shipingConverter.contains("over")) {
                        item.setPriceShipping(price + 5);
                        shipingConverter = "Этот текст здесь нужен";
                    }
                    if (shipingConverter.contains("FREE")) {
                        item.setPriceShipping(price);
                    }
                    if (shipingConverter.contains("$")) {
                        shipingConverter = shipingConverter.substring(2).split(" ")[0];
                        item.setPriceShipping(price + Double.parseDouble(shipingConverter));
                    }
                    if (shipingConverter.length() < 1) {
                        item.setPriceShipping(price);
                    }
                } catch (Exception ignored) {
                }

                String brand = "";
                try {
                    Elements brandEl = doc.select("#product-specification-table tr");
                    for (Element el : brandEl) {
                        if (el.text().contains("Brand")) {
                            brand = el.select("td").text();
                        }
                    }
                    if (brand.length() < 1) {
                        brandEl = doc.select("th");
                        for (Element el : brandEl) {
                            if (el.text().contains("Brand Name")) {
                                brand = el.parent().select("td").text();
                            }
                        }
                    }
//                    if (brand.length() < 1) {
//                        brand = doc.select("#productDescription").text();
//                        if (brand.contains("Brand Story")) {
//                            brand = brand.split("Brand Story")[1];
//                        } else {
//                            brand = "";
//                        }
//                    }
                } catch (Exception ignored) {
                }
                item.setBrand(brand.trim());


                String partNumber = "";
                try {
                    Elements partNumberEl = doc.select("th");
                    for (Element el : partNumberEl) {
                        if (el.text().contains("Part Number")) {
                            partNumber = el.parent().select("td").text();
                        }
                    }
                } catch (Exception ignored) {
                }
                item.setPartNumber(partNumber.trim());

                String itemModelNumber = "";
                try {
                    Elements itemModelNumberEl = doc.select("th");
                    for (Element el : itemModelNumberEl) {
                        if (el.text().contains("Item model number")) {
                            itemModelNumber = el.parent().select("td").text();
                        }
                    }
                    if (itemModelNumber.length() < 1) {
                        itemModelNumberEl = doc.select("li");
                        for (Element el : itemModelNumberEl) {
                            if (el.text().contains("Item model number")) {
                                itemModelNumber = el.text();
                                itemModelNumber = itemModelNumber.split(":")[1];
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
                item.setItemModelNumber(itemModelNumber.trim());


                String asinDomin = "";
                try {
                    Elements asinDominEl = doc.select("th");
                    for (Element el : asinDominEl) {
                        if (el.text().contains("ASIN")) {
                            asinDomin = el.parent().select("td").text();
                        }
                    }
                    if (asinDomin.length() < 1) {
                        asinDominEl = doc.select("li");
                        for (Element el : asinDominEl) {
                            if (el.text().contains("ASIN")) {
                                asinDomin = el.text();
                                asinDomin = asinDomin.split(":")[1];
                            }
                        }
                    }
                    if (asinDomin.length() < 1) {
                        asinDominEl = doc.select("td");
                        for (Element el : asinDominEl) {
                            if (el.text().contains("ASIN")) {
                                asinDomin = el.parent().select("td.value").text();
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
                item.setAsinDomin(asinDomin.trim());

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
                        for (Element el : select) {
                            if (el.text().contains("Customer Reviews")) {
                                textRating = el.parent().select("td.value span").text();
                                textRating = textRating.split(" out")[0];
                            }
                        }
                    }
                    rating = Double.parseDouble(textRating);
                } catch (Exception ignored) {
                }
                item.setRating(rating);

                String quantity = "0";
                try {
                    quantity = doc.select("#acrCustomerReviewText").text();
                    if (!quantity.equals("")) {
                        quantity = quantity.split(" customer")[0];
                    }
                    if (quantity.equals("")) {
                        quantity = "0";
                    }
                } catch (Exception ignored) {
                }
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
                        bSRDouble = getBSR(doc, "#SalesRank");
                    }
                    bSR = Integer.parseInt(bSRDouble);
                } catch (Exception ignored) {
                }
                item.setbSR(bSR);

                String bSRCategory = "";
                try {
                    Elements bSRCategoryEl = doc.select("th");

                    for (Element el : bSRCategoryEl) {
                        if (el.text().contains("Best Sellers Rank")) {
                            bSRCategory = el.parent().select("td").text();
                            bSRCategory = bSRCategory.split("in ")[1];
                            bSRCategory = bSRCategory.split("\\(S")[0];
                        }
                    }
                    if (bSRCategory.length() < 1) {
                        bSRCategory = getbSRCategory(doc, "#SalesRank");
                    }
                    bSRCategory = bSRCategory.split(">")[0];
                } catch (Exception ignored) {
                }
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
                    new SimpleDateFormat("yyyy.MM.dd").format(dateFirstAvailable);
                } catch (Exception ignored) {
                }
                item.setDateFirstAvailable(dateCreation);

                Boolean offerStatus = false;
                try {
                    String newHref = doc.select("#olp_feature_div").text();
                    if (newHref.toLowerCase().contains("new")) {
                        if (doc.select("#olp_feature_div a").attr("href").contains(item.getAsin())) {
                            offerStatus = true;
                        }
                    }
                    if (!offerStatus) {
                        newHref = doc.select("#moreBuyingChoices_feature_div").text();
                        if (newHref.toLowerCase().contains("new")) {
                            if (doc.select("#moreBuyingChoices_feature_div a").attr("href").contains(item.getAsin())) {
                                offerStatus = true;
                            }
                        }
                    }
                    if (!offerStatus) {
                        newHref = doc.select("#toggleBuyBox").text();
                        if (newHref.toLowerCase().contains("new")) {
                            if (doc.select("#toggleBuyBox a").attr("href").contains(item.getAsin())) {
                                offerStatus = true;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
                item.setNew(offerStatus);

                HashSet<String> searchReq = new HashSet<>();
                try {
                    if (!vendor.isEmpty() && !partNumber.isEmpty())
                        searchReq.add(String.format("%s + %s", item.getVendor(), item.getPartNumber()));

                    if (!vendor.isEmpty() && !itemModelNumber.isEmpty())
                        searchReq.add(String.format("%s + %s", item.getVendor(), item.getItemModelNumber()));

                    if (!brand.isEmpty() && !partNumber.isEmpty())
                        searchReq.add(String.format("%s + %s", item.getBrand(), item.getPartNumber()));

                    if (!brand.isEmpty() && !itemModelNumber.isEmpty())
                        searchReq.add(String.format("%s + %s", item.getBrand(), item.getItemModelNumber()));
                } catch (Exception ignored) {}
                if (!item.getPromoOffer() && (item.getPartNumber().isEmpty() || item.getItemModelNumber().isEmpty()))
                    continue;

                item.setSearchReq(searchReq);

                // Здесь уже норм код
                result.add(item);
            }
        }
        log.info("Processing time: " + (new Date().getTime() - start) + " ms");
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
        log.info("Begin processing the promo");

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
                Elements priceNewEl = doc.select("div.olpOffer");

                for (int i = 0; i < (priceNewEl.size() > 5 ? 5 : priceNewEl.size()); i++) {
                    Offer offer = new Offer();
                    Element el = priceNewEl.get(i);

                    String shipInfo = "";
                    try {
                        shipInfo = el.select("p.olpShippingInfo").text();
                    } catch (Exception ignore) {}
                    offer.setShipingInfo(shipInfo);

                    String shipPrice = "";
                    try {
                        shipPrice = el.select(".olpPriceColumn span").get(0).text();
                    } catch (Exception ignore) {}
                    offer.setPrice(shipPrice);

                    String sellerNew = "";
                    try {
                       sellerNew = el.select(".olpSellerColumn h3").text();
                        if (sellerNew.isEmpty())
                            sellerNew = el.select(".olpSellerColumn h3 img").attr("alt");
                    } catch (Exception ignore) {}
                    offer.setSeller(sellerNew);


//            //TODO Заменить 5 на значение пользователя
                    Double addPrice = 0.00;
                    String shipingConverterOffer = offer.getShipingInfo();
                    try {
                        if (shipingConverterOffer.contains("over")) {
                            addPrice = Double.parseDouble(offer.getPrice().substring(1));
                            addPrice = addPrice + Double.parseDouble(System.getProperty("addPrice"));
                        }
                        if (addPrice < 1) {
                            if (shipingConverterOffer.contains("FREE")) {
                                addPrice = Double.parseDouble(offer.getPrice().substring(1));
                            }
                        }
                            if (shipingConverterOffer.contains("+ $")) {
                            if (addPrice < 1) {
                                shipingConverterOffer = shipingConverterOffer.substring(3).split(" ")[0];
                                addPrice = Double.parseDouble(offer.getPrice().substring(1));
                                addPrice = addPrice + Double.parseDouble(shipingConverterOffer);
                            }
                        } else if (shipingConverterOffer.length() < 1) {
                            addPrice = Double.parseDouble(offer.getPrice().substring(1));
                        }
                    } catch (Exception ignore) {}
                    offer.setPriceShipingInfo(addPrice);



                    priceNew.add(offer);



                }
            } catch (Exception ignored) {
            }


//            try {
//                Offer offer = new Offer();
//
//            } catch (Exception ignored) {
//            }
//
            item.setOffers(priceNew);



            // Здесь уже норм код
            result.add(item);
        }

        log.info("Processing time: " + (new Date().getTime() - start) + " ms");

        return result;
    }

    public static List<Search> parseSearchReq(List<RequestTask> tasks) {

        log.info("-------------------------------------------------");
        log.info("Begin processing search results");

        long start = new Date().getTime();

        ArrayList<Search> result = new ArrayList<>();
        for (RequestTask task : tasks) {
            Search item = new Search();
            item.setRelatedAsin(task.getId().substring(0, task.getId().length() - 2));

            Document doc = Jsoup.parse(task.getHtml());
            // Здесь гавнарит Александр

            List<String> asins = new ArrayList<>();
            try {
                Elements asinNewEl = doc.select("li[id^='result']");
                for (int i = 0; i < asinNewEl.size(); i++) {
                    Element el = asinNewEl.get(i);
                    asins.add(el.attr("data-asin"));
                }
            } catch (Exception ignored) {
            }
            item.setAsins(asins);

            // Здесь уже норм код
            result.add(item);
        }

        log.info("Processing time: " + (new Date().getTime() - start) + " ms");

        return result;
    }

    public static HashSet<String> parseCategory(List<RequestTask> tasks) {

        log.info("-------------------------------------------------");
        log.info("Begin collecting ASINS by page category");

        long start = new Date().getTime();

        HashSet<String> result = new HashSet<>();
        for (RequestTask task : tasks) {
            Document doc = Jsoup.parse(task.getHtml());

            try {
                Elements allItems = doc.select("li[id^=result]");
                for (Element item : allItems) {
                    String asin = item.attr("data-asin");
                    result.add(asin);
                }
            } catch (Exception ignored) {}
        }

        log.info("Processing time: " + (new Date().getTime() - start) + " ms");

        return result;
    }

    public static List<ItemShortInfo> parseShortItems(List<RequestTask> tasks) {

        log.info("-------------------------------------------------");
        log.info("Begin processing amazon search pages");

        long start = new Date().getTime();

        ArrayList<ItemShortInfo> result = new ArrayList<>();
        for (RequestTask task : tasks) {
            ItemShortInfo shortInfo = new ItemShortInfo();
            shortInfo.setAsin(task.getId());

            Document doc = Jsoup.parse(task.getHtml());
            // Начало обработки














            /*
            detail-bullets_feature_div : B01BHUSR38
            productDetails_feature_div : B000W8J67S
            detail-bullets : B000K6TF1Y
            prodDetails : B00LSOURPA
            detailBullets : B075NZC7PR
            technicalSpecifications_feature_div : B016P70092
             */

            ArrayList<String> params = new ArrayList<>();
            try {
                if (doc.select("div#detail-bullets_feature_div").size() > 0) {
                    Elements paramsEl = doc.select("div#detail-bullets_feature_div table ul li");
                    for (Element el : paramsEl) {
                        try {
                        el.select("b").remove();
                        params.add(el.text());
                        }catch (Exception ignore) {}
                    }
                } else if (doc.select("div#productDetails_feature_div").size() > 0) {
                    Elements paramsEl = doc.select("div#productDetails_feature_div td");
                    for (Element el : paramsEl) {
                        try {
                        params.add(el.text());
                        }catch (Exception ignore) {}
                    }
                } else if (doc.select("div#detail-bullets").size() > 0) {
                    Elements paramsEl = doc.select("div#detail-bullets .content li");
                    for (Element el : paramsEl) {
                        try {
                        el.select("b").remove();
                        params.add(el.text());
                        }catch (Exception ignore) {}
                    }
                } else if (doc.select("div#prodDetails").size() > 0) {
                    Elements paramsEl = doc.select("div#prodDetails tr");
                    for (Element el : paramsEl) {
                        try {
                        params.add(el.select("td").get(1).text());
                        }catch (Exception ignore) {}
                    }
                } else if (doc.select("div#detailBullets").size() > 0) {
                    Elements paramsEl = doc.select("div#detailBullets ul");
                    Elements paramsEl1 = paramsEl.get(0).select("li");
                    Elements paramsEl2 = paramsEl.get(1).select("li");
                    Elements paramsEl3 = paramsEl.get(2).select("li");
                    for (Element el : paramsEl1) {
                        try {
                            params.add(el.select("span span").get(1).text());
                        }catch (Exception ignore) {}
                    }
                    for (Element el : paramsEl2) {
                        try {
                        params.add(el.select("span").get(1).text());
                        }catch (Exception ignore) {}
                    }
                    for (Element el : paramsEl3) {
                        try {
                        params.add(el.select("span div").text());
                        }catch (Exception ignore) {}
                    }
                } else if (doc.select("div#technicalSpecifications_feature_div").size() > 0) {
                    Elements paramsEl = doc.select("div#technicalSpecifications_feature_div td");
                    for (Element el : paramsEl) {
                        try {
                            params.add(el.text());
                        }catch (Exception ignore) {}
                    }
                } else {
                    log.info("===========================================");
                    log.info("Fail: " + task.getId());
                    log.info("===========================================");
                }
            } catch (Exception ignore) {ignore.printStackTrace();}
            shortInfo.setParams(params);

            String vendor = "";
            try {
                vendor = doc.select("a#bylineInfo").text();
                if (vendor.isEmpty()) {
                    vendor = doc.select("a#brand").text();
                }
            } catch (Exception ignored) {}
            shortInfo.setVendor(vendor);

            Boolean availability = false;
            try {
                Elements inStockStatus = doc.select("div#availability");
                availability = !inStockStatus.text().contains("unavailable");
            } catch (Exception ignored) {
            }
            shortInfo.setAvailability(availability);

            Boolean offerStatus = false;
            try {
                String newHref = doc.select("#olp_feature_div").text();
                if (newHref.toLowerCase().contains("new")) {
                    if (doc.select("#olp_feature_div a").size() > 0) {
                        offerStatus = true;
                    }
                }
                if (!offerStatus) {
                    newHref = doc.select("#moreBuyingChoices_feature_div").text();
                    if (newHref.toLowerCase().contains("new")) {
                        if (doc.select("#moreBuyingChoices_feature_div a").size() > 0) {
                            offerStatus = true;
                        }
                    }
                }
                if (!offerStatus) {
                    newHref = doc.select("#toggleBuyBox").text();
                    if (newHref.toLowerCase().contains("new")) {
                        if (doc.select("#toggleBuyBox a").size() > 0) {
                            offerStatus = true;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
            shortInfo.setIsNew(offerStatus);


            result.add(shortInfo);
        }


        log.info("Processing time: " + (new Date().getTime() - start) + " ms");

        return result;
    }
}
