package parser;

public class ItemShortInfo {

    private String asin;
    private Boolean availability;
    private Boolean isNew;
    private Offer firstOffer;

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
