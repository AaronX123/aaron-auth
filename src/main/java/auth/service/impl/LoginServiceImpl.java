package auth.service.impl;

import aaron.common.data.common.CacheConstants;
import aaron.common.data.exception.StarterError;
import aaron.common.utils.CommonUtils;
import aaron.common.utils.SnowFlake;
import aaron.common.utils.jwt.JwtUtil;
import aaron.common.utils.jwt.UserPermission;
import auth.common.AuthError;
import auth.common.AuthException;
import auth.dao.*;
import auth.pojo.dto.UserDto;
import auth.pojo.model.*;
import auth.service.LoginService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    UserRoleDao userRoleDao;

    @Resource
    RoleDao roleDao;

    @Resource
    RoleResourceDao roleResourceDao;

    @Resource
    ResourceDao resourceDao;

    @Resource
    UserOnlineInfoDao userOnlineInfoDao;

    @Autowired
    SnowFlake snowFlake;

    @Autowired
    CacheManager cacheManager;

    @Transactional(rollbackFor = Exception.class)
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
        user = findById(userPermission.getId());
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


    private User findById(long id){
        User user = userDao.selectId(id);
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq("user_id",id);
        List<UserRole> userRoleList = userRoleDao.selectList(userRoleQueryWrapper);
        List<Role> roleList = new ArrayList<>();
        for (UserRole userRole : userRoleList) {
            roleList.add(roleDao.selectId(userRole.getRoleId()));
        }
        List<RoleResource> roleResourceList = new ArrayList<>();
        for (Role role : roleList) {
            QueryWrapper<RoleResource> roleResourceQueryWrapper = new QueryWrapper<>();
            roleResourceQueryWrapper.eq("role_id",role.getId());
            List<RoleResource> roleResource = roleResourceDao.selectList(roleResourceQueryWrapper);
            roleResourceList.addAll(roleResource);
        }
        List<auth.pojo.model.Resource> resourceList = resourceDao.selectBatchIds(roleResourceList.stream().map(RoleResource::getResourceId).collect(Collectors.toList()));
        user.setRoles(new HashSet<>(roleList));
        for (Role role : roleList) {
            Set<auth.pojo.model.Resource> set = new HashSet<>();
            for (RoleResource roleResource : roleResourceList) {
                if (role.getId().equals(roleResource.getRoleId())){
                    for (auth.pojo.model.Resource resource : resourceList) {
                        if (resource.getId().equals(roleResource.getResourceId())){
                            set.add(resource);
                        }
                    }
                }
            }
            role.setResources(set);
        }
        return user;
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
            QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
            userRoleQueryWrapper.eq("user_id",userPermission.getId());
            List<UserRole> userRole = userRoleDao.selectList(userRoleQueryWrapper);
            Set<auth.pojo.model.Resource> resourceSet = new LinkedHashSet<>();
            for (UserRole role : userRole) {
                QueryWrapper<RoleResource> roleResourceQueryWrapper = new QueryWrapper<>();
                roleResourceQueryWrapper.eq("role_id",role.getRoleId());
                List<RoleResource> roleResourceList = roleResourceDao.selectList(roleResourceQueryWrapper);
                resourceSet.addAll(resourceDao.listByIdList(roleResourceList.stream().map(RoleResource::getResourceId).collect(Collectors.toList())));
            }
            return CommonUtils.convertList(resourceSet,UserMenu.class);
        } catch (Exception e) {
            throw new AuthException(StarterError.SYSTEM_ACCESS_INVALID);
        }
    }

    @Override
    public boolean logout(List<Long> ids) {
        Cache cache = cacheManager.getCache(CacheConstants.TOKEN);
        Cache resourceCache = cacheManager.getCache(CacheConstants.RESOURCE_MAP);
        for (Long id : ids) {
            Cache.ValueWrapper valueWrapper = cache.get(id);
            if (valueWrapper != null){
                resourceCache.evict(id);
                UserPermission userPermission;
                try {
                    userPermission = JwtUtil.parseJwt(String.valueOf(valueWrapper.get()));
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
