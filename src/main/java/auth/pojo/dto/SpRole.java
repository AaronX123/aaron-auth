package auth.pojo.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-17
 */
public class SpRole {
    /**
     * 角色ID
     */
    private Long id;
    /**
     * 角色名
     */
    private String name;

    Set<SpResource> resources;

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

    public Set<SpResource> getResources() {
        return resources;
    }

    public void setResources(Set<SpResource> resources) {
        this.resources = resources;
    }
}
