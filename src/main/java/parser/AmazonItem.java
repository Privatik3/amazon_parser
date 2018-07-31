package parser;

public class AmazonItem {

    private String asin;
    private Boolean availability;
    private Boolean promoOffer;
    private String vendor;
    private String productName;
    private String buyBoxSeller;
    private Double buyBoxPrice;
    private String buyBoxShipping;
    private String brand;
    private String partNumber;
    private String itemModelNumber;
    private String asinDomin;
    private Double rating;
    private String quantity;
    private Double bSR;
    private String bSRCategory;
    private String dateFirstAvailable;

    @Override
    public String toString() {
        return "AmazonItem{" +
                "asin='" + asin + '\'' +
                ", availability=" + availability +
                ", promoOffer=" + promoOffer +
                ", vendor='" + vendor + '\'' +
                ", productName='" + productName + '\'' +
                ", buyBoxSeller='" + buyBoxSeller + '\'' +
                ", buyBoxPrice='" + buyBoxPrice + '\'' +
                ", buyBoxShipping='" + buyBoxShipping + '\'' +
                ", brand='" + brand + '\'' +
                ", partNumber=" + partNumber +
                ", itemModelNumber=" + itemModelNumber +
                ", asinDomin='" + asinDomin + '\'' +
                ", rating=" + rating +
                ", quantity=" + quantity +
                ", bSR=" + bSR +
                ", bSRCategory='" + bSRCategory + '\'' +
                ", dateFirstAvailable='" + dateFirstAvailable + '\'' +
                '}';
    }

    public Boolean getPromoOffer() {
        return promoOffer;
    }

    public void setPromoOffer(Boolean promoOffer) {
        this.promoOffer = promoOffer;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBuyBoxSeller() {
        return buyBoxSeller;
    }

    public void setBuyBoxSeller(String buyBoxSeller) {
        this.buyBoxSeller = buyBoxSeller;
    }

    public Double getBuyBoxPrice() {
        return buyBoxPrice;
    }

    public void setBuyBoxPrice(Double buyBoxPrice) {
        this.buyBoxPrice = buyBoxPrice;
    }

    public String getBuyBoxShipping() {
        return buyBoxShipping;
    }

    public void setBuyBoxShipping(String buyBoxShipping) {
        this.buyBoxShipping = buyBoxShipping;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getItemModelNumber() {
        return itemModelNumber;
    }

    public void setItemModelNumber(String itemModelNumber) {
        this.itemModelNumber = itemModelNumber;
    }

    public String getAsinDomin() {
        return asinDomin;
    }

    public void setAsinDomin(String asinDomin) {
        this.asinDomin = asinDomin;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Double getbSR() {
        return bSR;
    }

    public void setbSR(Double bSR) {
        this.bSR = bSR;
    }

    public String getbSRCategory() {
        return bSRCategory;
    }

    public void setbSRCategory(String bSRCategory) {
        this.bSRCategory = bSRCategory;
    }

    public String getDateFirstAvailable() {
        return dateFirstAvailable;
    }

    public void setDateFirstAvailable(String dateFirstAvailable) {
        this.dateFirstAvailable = dateFirstAvailable;
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
}
