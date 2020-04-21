package auth.pojo.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-17
 */
@Data
@Accessors(chain = true)
public class RoleResource {
    long id;
    long roleId;
    long resourceId;
    int type;
}
