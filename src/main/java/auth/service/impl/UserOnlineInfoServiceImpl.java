package auth.service.impl;

import auth.dao.UserOnlineInfoDao;
import auth.pojo.model.UserOnlineInfo;
import auth.service.UserOnlineInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserOnlineInfoServiceImpl extends ServiceImpl<UserOnlineInfoDao, UserOnlineInfo> implements UserOnlineInfoService {
}
