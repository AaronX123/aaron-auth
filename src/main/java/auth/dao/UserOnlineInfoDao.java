package auth.dao;

import auth.pojo.model.UserOnlineInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-05
 */
@Mapper
public interface UserOnlineInfoDao extends BaseMapper<UserOnlineInfo> {

    @Select("SELECT * FROM user_online_info WHERE status = 1")
    List<UserOnlineInfo> listOnlineUser();

    @Select("<script>" +
            "UPDATE user_online_info SET status = '0' WHERE user_id IN" +
            "<foreach collection=\"ids\" item=\"id\" separator=\",\" close=\")\" open=\"(\">\n" +
            "            #{id}\n" +
            "        </foreach>" +
            "</script>")
    boolean updateOnlineState(@Param("ids")List<Long> ids);

}
