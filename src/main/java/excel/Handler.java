package excel;

import parser.AmazonItem;
import parser.ItemShortInfo;
import parser.Offer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Handler {

    public static List<String> readListOfAsin(String pathToListing) {

        List<String> result = new ArrayList<>();
        try {
            result = Files.readAllLines(Paths.get(pathToListing));
        } catch (Exception e) {
            System.err.println("Не удалось загрузить листинг");
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {

        List<AmazonItem> result = new ArrayList<>();
        AmazonItem item = new AmazonItem();

        item.setAsin("B01M7W3L8U");
        item.setAvailability(true);
        item.setPromoOffer(false);
        item.setVendor("Le Creuset");
        item.setProductName("Le Creuset 7 1/4 Qt. Cast Iron Round French Oven - Dune");
        item.setBuyBoxSeller("K & J Products");
        item.setBuyBoxPrice(7.95);
        item.setBuyBoxShipping("& FREE shipping on orders over $25.00 shipped by Amazon.");
        item.setBrand("Clorox");
        item.setPartNumber("KJDPV");
        item.setItemModelNumber("060740-002-0000");
        item.setAsinDomin("B01K7MXF0W");
        item.setRating(4.4);
        item.setQuantity("36");
        item.setbSR(27791);
        item.setbSRCategory("Beauty & Personal Care");
        item.setDateFirstAvailable(new Date());

        // OFFERS
        List<Offer> offers = new ArrayList<>();

        // First
        Offer firstOffer = new Offer();
        firstOffer.setPrice("$429.99");
        firstOffer.setShipingInfo("+ $4.49shipping");
        firstOffer.setSeller("Premium Pots");
        offers.add(firstOffer);

        item.setOffers(offers);

        // Second
        Offer secondOffer = new Offer();
        secondOffer.setPrice("$87.10");
        secondOffer.setShipingInfo("& FREE Shipping");
        secondOffer.setSeller("BBBProducts");
        offers.add(secondOffer);

        item.setOffers(offers);

        // SEARCH INFO
        List<ItemShortInfo> searchInfo = new ArrayList<>();
        ItemShortInfo info = new ItemShortInfo();
        info.setAsin("B07892Z5SH");
        info.setAvailability(true);
        info.setFirstOffer(firstOffer);
        searchInfo.add(info);

        item.setSearchInfo(searchInfo);

        result.add(item);
//        result.add(item);

        writeResult(result);
    }

    public static void writeResult(List<AmazonItem> result) {

        String saveFile = "result.csv";
        // TODO Нужно сформировать и сохранить Excel файл
        // тестовая инфа уже забита выше, в методе main
        // можешь запускать через правую кнопку и тестить как всё работате

        try {
            for (AmazonItem item : result)
                Files.write(Paths.get(saveFile), (item.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Не удалось сохранить отчёт");
            e.printStackTrace();
        }
    }
}
