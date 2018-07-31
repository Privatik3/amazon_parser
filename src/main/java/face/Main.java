package face;

import parser.ItemType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParseException {

        String urlListing = "";
        String pathToListing = "";
        String pathToBadSellers = "";

        // ----------------------------------
        Integer countOfFibers = 512;
        String usaZipCode = "07064";
        Double addToPrice = 6.0;

        // ----------------------------------
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(ItemType.NONE, 20000, 600000, true));
        filters.add(new Filter(ItemType.UNAVALIABLE, 20000, 600000, true));
        filters.add(new Filter(ItemType.PRIME, 20000, 600000, true));

        // ----------------------------------
        Double ratingMin = 3.0;
        Double ratingMax = 5.0;

        Date creationFrom = new SimpleDateFormat("MM/dd/yyyy").parse("4/22/2012");
        Date creationTo = new SimpleDateFormat("MM/dd/yyyy").parse("4/22/2018");


    }
}
