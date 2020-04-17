package auth.pojo.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author CJF
 * @version V1.0.0
 * @date 2019/9/9
 */
@Data
@Accessors(chain = true)
public class Role extends Model<Role> implements Serializable {
    private static final long serialVersionUID = 3372896897477467061L;
    /**
     * 角色ID
     */
    private Long id;
    /**
     * 角色名
     */
    private String name;

    private Set<Resource> resources = new HashSet<>();

    @Override
    protected Serializable pkVal() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }
}
