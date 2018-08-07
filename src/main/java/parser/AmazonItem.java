package parser;

import java.text.SimpleDateFormat;
import java.util.*;

public class AmazonItem {

    private String asin;
    private Boolean availability;
    private Boolean promoOffer;
    private String vendor = "";
    private String productName = "";
    private String buyBoxSeller;
    private Double buyBoxPrice;
    private String buyBoxShipping;
    private String brand;
    private String partNumber;
    private String itemModelNumber;
    private String asinDomin;
    private Double rating;
    private String quantity;
    private Integer bSR;
    private String bSRCategory;
    private Date dateFirstAvailable;
    private Boolean isNew;
    private HashSet<String> searchReq = new HashSet<>();
    private List<Offer> offers = new ArrayList<>();
    private List<ItemShortInfo> searchInfo = new ArrayList<>();


    @Override
    public String toString() {
        String result =
                asin + ';' +
                availability + ';' +
                promoOffer + ';' +
                vendor.replaceAll(";", ",") + ';' +
                productName.replaceAll(";", ",") + ';' +
                buyBoxSeller + ';' +
                buyBoxPrice + ';' +
                buyBoxShipping + ';' +
                brand + ';' +
                partNumber + ';' +
                itemModelNumber + ';' +
                asinDomin + ';' +
                rating + ';' +
                quantity + ';' +
                bSR + ';' +
                bSRCategory + ';' +
                new SimpleDateFormat("yyyy.MM.dd").format(dateFirstAvailable) + ';';
//                newHref + ';';

        result += "OFFERS: ";
        for (Offer offer : offers) {
            result += offer + " | ";
        }
        result = result.substring(0, result.length() - (offers.size() > 0 ? 3 : 1)) + ";";

        result += "SHORT_INFO: ";
        for (ItemShortInfo info : searchInfo) {
            result += info + " | ";
        }
        result = result.substring(0, result.length() - (searchInfo.size() > 0 ? 3 : 1)) + ";";

        return result.substring(0, result.length() -  1);
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public List<ItemShortInfo> getSearchInfo() {
        return searchInfo;
    }

    public void setSearchInfo(List<ItemShortInfo> searchInfo) {
        this.searchInfo = searchInfo;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public HashSet<String> getSearchReq() {
        return searchReq;
    }

    public void setSearchReq(HashSet<String> searchReq) {
        this.searchReq = searchReq;
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

    public Integer getbSR() {
        return bSR;
    }

    public void setbSR(Integer bSR) {
        this.bSR = bSR;
    }

    public String getbSRCategory() {
        return bSRCategory;
    }

    public void setbSRCategory(String bSRCategory) {
        this.bSRCategory = bSRCategory;
    }

    public Date getDateFirstAvailable() {
        return dateFirstAvailable;
    }

    public void setDateFirstAvailable(Date dateFirstAvailable) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AmazonItem that = (AmazonItem) o;
        return Objects.equals(asin, that.asin);
    }

    @Override
    public int hashCode() {

        return Objects.hash(asin);
    }
}
