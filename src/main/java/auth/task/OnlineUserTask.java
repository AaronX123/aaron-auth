package auth.task;

import aaron.common.data.common.CacheConstants;
import aaron.common.utils.CommonUtils;
import auth.dao.UserOnlineInfoDao;
import auth.pojo.model.UserOnlineInfo;
import auth.service.UserOnlineInfoService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时确认在线人员
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-23
 */
@Slf4j
@Service
public class OnlineUserTask {
    @Autowired
    UserOnlineInfoService userOnlineInfoService;

    @Resource
    UserOnlineInfoDao userOnlineInfoDao;

    @Autowired
    CacheManager cacheManager;

    @Scheduled(cron = "${check.user}")
    public void check(){
        log.info("开始定时处理离线用户");
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
        if (CommonUtils.isEmpty(updateToLogoutId)){
            log.info("离线用户处理完成");
            return;
        }
        for (Long id : updateToLogoutId) {
            UpdateWrapper<UserOnlineInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status",0);
            updateWrapper.eq("user_id",id);
            updateWrapper.set("offline_time",new Date());
            userOnlineInfoService.update(updateWrapper);
        }
        log.info("离线用户处理完成");
    }
}
