package parser;

import java.util.ArrayList;
import java.util.List;

public class ItemOffer {

    private String asin;
    private List<Offer> offers = new ArrayList<>();

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}
