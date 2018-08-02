package manager;

public class RequestTask {

    private String id;
    private String url;
    private ReqTaskType type;

    private String html;

    public RequestTask(String id) {
        this.id = id;
    }

    public RequestTask(String id, String html) {
        this.id = id;
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ReqTaskType getType() {
        return type;
    }

    public void setType(ReqTaskType type) {
        this.type = type;
    }
}
