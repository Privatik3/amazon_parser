package parser;

public class EbayItem {

    private String itemNumber;
    private Double price;
    private String seller;
    private String shipping;
    private Double priceShipping;

    public Double getPriceShipping() {
        return priceShipping;
    }

    public void setPriceShipping(Double priceShipping) {
        this.priceShipping = priceShipping;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

}
