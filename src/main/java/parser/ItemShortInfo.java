package parser;

import java.util.ArrayList;

public class ItemShortInfo {

    private String asin;
    private Boolean availability;
    private Boolean isNew;
    private Offer firstOffer;

    private ArrayList<String> params;
    private String vendor;

    public ArrayList<String> getParams() {
        return params;
    }

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public Offer getFirstOffer() {
        return firstOffer;
    }

    public void setFirstOffer(Offer firstOffer) {
        this.firstOffer = firstOffer;
    }

    @Override
    public String toString() {
        return "[asin='" + asin + '\'' +
                ", availability=" + availability +
                ", firstOffer=" + firstOffer + ']';
    }
}
