package auth.pojo.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-17
 */
public class SpUser {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 工号
     */
    private String code;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String profilePicture;

    private Set<SpRole> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Set<SpRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SpRole> roles) {
        this.roles = roles;
    }
}
