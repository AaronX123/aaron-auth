package auth.service.impl;

import aaron.common.data.common.CacheConstants;
import aaron.common.data.exception.StarterError;
import aaron.common.utils.CommonUtils;
import aaron.common.utils.SnowFlake;
import aaron.common.utils.jwt.JwtUtil;
import aaron.common.utils.jwt.UserPermission;
import auth.common.AuthError;
import auth.common.AuthException;
import auth.dao.UserDao;
import auth.dao.UserOnlineInfoDao;
import auth.pojo.dto.UserDto;
import auth.pojo.model.*;
import auth.service.LoginService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-15
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    UserDao userDao;

    @Resource
    UserOnlineInfoDao userOnlineInfoDao;

    @Autowired
    SnowFlake snowFlake;

    @Autowired
    CacheManager cacheManager;


    @Override
    public Map<String, Object> createToken(UserDto userDto) {
        UserPermission userPermission;
        User user = CommonUtils.copyProperties(userDto,User.class);
        try {
            userPermission = userDao.checkUser(user);
            if (userPermission == null){
                throw new AuthException(AuthError.USER_NOT_EXIST);
            }
        }catch (Exception e){
            throw new AuthException(AuthError.USER_NOT_EXIST);
        }
        UserOnlineInfo userOnlineInfo = CommonUtils.copyProperties(userDto,UserOnlineInfo.class);
        userOnlineInfo.setId(snowFlake.nextId());
        userOnlineInfo.setUserId(userPermission.getId());
        userOnlineInfo.setName(userPermission.getUserName());
        userOnlineInfo.setOnlineTime(new Date());
        userOnlineInfo.setStatus((byte) 1);
        if (userOnlineInfoDao.insert(userOnlineInfo) != 1){
            throw new AuthException(AuthError.ONLINE_INSERT_FAIL);
        }
        userPermission.setUserOnlineId(userOnlineInfo.getId());
        String token = JwtUtil.createJwt(userPermission);
        Cache tokenCache = cacheManager.getCache(CacheConstants.TOKEN);
        // 根据userId查询是否已经有缓存，如果有token说明已经登录
        Cache.ValueWrapper valueWrapper = tokenCache.get(userPermission.getId());
        if (valueWrapper != null){
            List<Long> ids = new ArrayList<>();
            ids.add(userPermission.getId());
            logout(ids);
        }
        // 更新token
        tokenCache.put(userPermission.getId(),token);
        user = userDao.findById(userPermission.getId());
        Map<String,String> urlMap = new HashMap<>();
        for (Role role : user.getRoles()) {
            for (auth.pojo.model.Resource resource : role.getResources()) {
                urlMap.put(String.valueOf(resource.getId()),resource.getUrl());
            }
        }
        // 更新资源
        Cache resourceCache = cacheManager.getCache(CacheConstants.RESOURCE_MAP);
        resourceCache.put(userPermission.getId(),urlMap);
        Map<String,Object> data = new HashMap<>(1);
        data.put("token",token);
        return data;
    }

    @Override
    public UserInfo getUserInfo(String token) {
        try {
            UserPermission userPermission = JwtUtil.parseJwt(token);
            return userDao.getUserInfo(userPermission);
        } catch (Exception e) {
            throw new AuthException(StarterError.SYSTEM_ACCESS_INVALID);
        }
    }

    @Override
    public List<UserMenu> getUserMenu(String token) {
        try {
            UserPermission userPermission = JwtUtil.parseJwt(token);
            return userDao.getUserMenu(userPermission);
        } catch (Exception e) {
            throw new AuthException(StarterError.SYSTEM_ACCESS_INVALID);
        }
    }

    @Override
    public boolean logout(List<Long> ids) {
        Cache cache = cacheManager.getCache(CacheConstants.USER_PERMISSION);
        for (Long id : ids) {
            Cache.ValueWrapper valueWrapper = cache.get(id);
            if (valueWrapper != null){
                UserPermission userPermission;
                try {
                    userPermission = JwtUtil.parseJwt(String.valueOf(valueWrapper));
                } catch (Exception e) {
                    throw new AuthException(StarterError.SYSTEM_TOKEN_PARSE_ERROR);
                }
                cache.evict(id);
                UserOnlineInfo userOnlineInfo = new UserOnlineInfo();
                userOnlineInfo.setId(userPermission.getUserOnlineId());
                userOnlineInfo.setUserId(userPermission.getId());
                userOnlineInfo.setStatus((byte) 0);
                userOnlineInfo.setOfflineTime(new Date());
                userOnlineInfoDao.updateById(userOnlineInfo);
            }
        }
        return true;
    }
}
