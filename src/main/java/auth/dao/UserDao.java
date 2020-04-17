package auth.dao;

import aaron.common.utils.jwt.UserPermission;
import auth.pojo.model.User;
import auth.pojo.model.UserInfo;
import auth.pojo.model.UserMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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


    @Select("SELECT user.id, user.code, user.name, user.password,\n" +
            "\t\t\trole.id as roleId, role.name as roleName,\n" +
            "\t\t\tresource.id as resourceId, resource.name as resourceName, resource.url as resourceUrl\n" +
            "      FROM user, user_role, role, role_resource, resource\n" +
            "      WHERE user.id = #{id}\n" +
            "        AND user.id = user_role.user_id\n" +
            "        AND user_role.role_id = role.id\n" +
            "        AND role.id = role_resource.role_id\n" +
            "        AND role_resource.resource_id = resource.id")
    User findById(long id);

    @Select("select id,name,profile_picture,company_id from t_user where id = #{id} ")
    UserInfo getUserInfo(UserPermission userPermission);

    @Select("SELECT distinct d.id,d.name,d.code,d.parent_id,d.url,d.open_img,d.close_img,d.resource_type " +
            "FROM t_role_resource a,t_user_role b,t_user c,t_resource d " +
            "WHERE a.role_id = b.role_id AND b.user_id = c.id AND d.id = a.resource_id AND c.id = #{id} " +
            "ORDER BY id")
    List<UserMenu> getUserMenu(UserPermission userPermission);

    @Select(" SELECT id, code, name, password\n" +
            "        FROM t_user\n" +
            "        WHERE code = #{code}")
    User findByCode(String code);
}
