package auth.dao;

import auth.pojo.model.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-17
 */
@Mapper
public interface UserRoleDao extends BaseMapper<UserRole> {
}
