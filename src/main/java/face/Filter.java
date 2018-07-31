package face;

public class Filter {

    private FilterType type;
    private Integer bsrFrom;
    private Integer bsrTo;
    private Boolean isEnable;

    public Filter(FilterType type, Integer bsrFrom, Integer bsrTo, Boolean isEnable) {
        this.type = type;
        this.bsrFrom = bsrFrom;
        this.bsrTo = bsrTo;
        this.isEnable = isEnable;
    }
}
