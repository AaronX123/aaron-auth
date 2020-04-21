package auth.dao;


import auth.pojo.model.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-05
 */
@Mapper
public interface ResourceDao extends BaseMapper<Resource> {
    @Select("<script>" +
            "SELECT * FROM resource WHERE id IN " +
            "<foreach collection=\"ids\" item=\"id\" separator=\",\" close=\")\" open=\"(\">\n" +
            "            #{id}\n" +
            "        </foreach>" +
            "</script>")
    List<Resource> listByIdList(@Param("ids") List<Long> ids);

}
