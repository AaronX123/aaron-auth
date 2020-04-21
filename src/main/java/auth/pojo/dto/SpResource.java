package auth.pojo.dto;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-17
 */
public class SpResource {
    /**
     * 资源Id
     */
    private Long id;
    /**
     * 资源名
     */
    private String name;
    /**
     * url
     */
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
