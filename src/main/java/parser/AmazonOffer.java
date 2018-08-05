package parser;

public class AmazonOffer {

    private String price;
    private String shipingInfo;
    private String seller;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShipingInfo() {
        return shipingInfo;
    }

    public void setShipingInfo(String shipingInfo) {
        this.shipingInfo = shipingInfo;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    @Override
    public String toString() {
        return "AmazonOffer{" +
                "price='" + price + '\'' +
                ", shipingInfo='" + shipingInfo + '\'' +
                ", seller='" + seller + '\'' +
                '}';
    }
}
