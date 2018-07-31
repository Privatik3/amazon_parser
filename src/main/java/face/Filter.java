package face;

import parser.ItemType;

public class Filter {

    private ItemType type;
    private Integer bsrFrom;
    private Integer bsrTo;
    private Boolean isEnable;

    public Filter(ItemType type, Integer bsrFrom, Integer bsrTo, Boolean isEnable) {
        this.type = type;
        this.bsrFrom = bsrFrom;
        this.bsrTo = bsrTo;
        this.isEnable = isEnable;
    }
}
