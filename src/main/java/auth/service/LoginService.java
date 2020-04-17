package auth.service;

import auth.pojo.dto.UserDto;
import auth.pojo.model.UserInfo;
import auth.pojo.model.UserMenu;

import java.util.List;
import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-15
 */
public interface LoginService {
    Map<String,Object> createToken(UserDto userDTO);
    UserInfo getUserInfo(String token);
    List<UserMenu> getUserMenu(String token);
    boolean logout(List<Long> ids);
}
