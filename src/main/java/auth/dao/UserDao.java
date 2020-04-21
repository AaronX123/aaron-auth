package auth.dao;

import aaron.common.utils.jwt.UserPermission;
import auth.pojo.model.User;
import auth.pojo.model.UserInfo;
import auth.pojo.model.UserMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-05
 */
@Mapper
public interface UserDao extends BaseMapper<User> {

    @Select("SELECT DISTINCT a.id,a.code,a.name AS userName,a.company_id,c.org_id FROM user a,user_role b,role c " +
            "where a.code = #{code} and a.id = b.user_id and b.role_id = c.id")
    UserPermission checkUser(User user);


    @Select("select id,name,profile_picture,company_id from user where id = #{id} ")
    UserInfo getUserInfo(UserPermission userPermission);

    @Select("SELECT distinct d.id,d.name,d.code,d.parent_id,d.url,d.open_img,d.close_img,d.resource_type " +
            "FROM role_resource a,user_role b,user c,resource d " +
            "WHERE a.role_id = b.role_id AND b.user_id = c.id AND d.id = a.resource_id AND c.id = #{id} " +
            "ORDER BY id")
    List<UserMenu> getUserMenu(UserPermission userPermission);

    @Select(" SELECT id, code, name, password\n" +
            "        FROM user\n" +
            "        WHERE code = #{code}")
    User findByCode(String code);

    @Select("SELECT id, code, name, password,profile_picture FROM user WHERE id = #{id}")
    User selectId(long id);
}
