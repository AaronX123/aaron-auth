package auth.task;

import aaron.common.data.common.CacheConstants;
import auth.dao.UserOnlineInfoDao;
import auth.pojo.model.UserOnlineInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时确认在线人员
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-23
 */
@Service
public class OnlineUserTask {

    @Resource
    UserOnlineInfoDao userOnlineInfoDao;

    @Autowired
    CacheManager cacheManager;

    @Scheduled(cron = "${check.user}")
    public void check(){
        // 查询在线的用户
        List<UserOnlineInfo> userOnlineInfoList = userOnlineInfoDao.listOnlineUser();
        Cache cache = cacheManager.getCache(CacheConstants.TOKEN);
        // 离线的Id
        List<Long> updateToLogoutId = new ArrayList<>();
        for (UserOnlineInfo userOnlineInfo : userOnlineInfoList) {
            Cache.ValueWrapper tokenWrapper = cache.get(userOnlineInfo.getUserId());
            if (tokenWrapper == null){
                updateToLogoutId.add(userOnlineInfo.getUserId());
            }
        }
        userOnlineInfoDao.updateOnlineState(updateToLogoutId);
    }
}
