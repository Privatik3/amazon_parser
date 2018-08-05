package face;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InterfaceParams {

    private String urlListing;
    private String pathToListing;
    private String pathToBadSellers;

    // ----------------------------------
    private Integer countOfFibers;
    private String usaZipCode;
    private Double addToPrice;

    // ----------------------------------
    private List<Filter> filters;

    public String getUrlListing() {
        return urlListing;
    }

    public void setUrlListing(String urlListing) {
        this.urlListing = urlListing;
    }

    public String getPathToListing() {
        return pathToListing;
    }

    public void setPathToListing(String pathToListing) {
        this.pathToListing = pathToListing;
    }

    public String getPathToBadSellers() {
        return pathToBadSellers;
    }

    public void setPathToBadSellers(String pathToBadSellers) {
        this.pathToBadSellers = pathToBadSellers;
    }

    public Integer getCountOfFibers() {
        return countOfFibers;
    }

    public void setCountOfFibers(Integer countOfFibers) {
        this.countOfFibers = countOfFibers;
    }

    public String getUsaZipCode() {
        return usaZipCode;
    }

    public void setUsaZipCode(String usaZipCode) {
        this.usaZipCode = usaZipCode;
    }

    public Double getAddToPrice() {
        return addToPrice;
    }

    public void setAddToPrice(Double addToPrice) {
        this.addToPrice = addToPrice;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }
}
