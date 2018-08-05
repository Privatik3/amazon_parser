package face;

public class Filter {

    private FilterType type;
    private double min;
    private double max;
    private Boolean isEnable;

    public Filter(FilterType type, double min, double max, Boolean isEnable) {
        this.type = type;
        this.min = min;
        this.max = max;
        this.isEnable = isEnable;
    }

    public Filter(FilterType type, long min, long max, Boolean isEnable) {
        this(type, (double) min, (double) max, isEnable);
    }

    public Filter(FilterType type, int min, int max, Boolean isEnable) {
        this(type, (double) min, (double) max, isEnable);
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public Boolean getEnable() {
        return isEnable;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }
}
