package auth.pojo.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-17
 */
@Data
@Accessors(chain = true)
public class UserRole extends Model<UserRole> {
    long id;
    long userId;
    long roleId;
}
