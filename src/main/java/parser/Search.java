package parser;

import java.util.List;

public class Search {

    private String relatedAsin;
    private List<String> asins;

    public String getRelatedAsin() {
        return relatedAsin;
    }

    public void setRelatedAsin(String relatedAsin) {
        this.relatedAsin = relatedAsin;
    }

    public List<String> getAsins() {
        return asins;
    }

    public void setAsins(List<String> asins) {
        this.asins = asins;
    }
}
