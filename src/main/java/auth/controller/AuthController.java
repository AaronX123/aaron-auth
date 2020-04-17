package auth.controller;

import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.utils.CommonUtils;
import auth.pojo.dto.UserDto;
import auth.pojo.model.UserInfo;
import auth.pojo.vo.UserVo;
import auth.service.LoginService;
import com.alibaba.fastjson.JSON;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author CJF
 * @version V1.0.0
 * @date 2019/8/26
 */
@RestController
public class AuthController {
    @Autowired
    private LoginService loginService;

    @Autowired
    CommonState state;

    @PostMapping(value = "/login")
    public CommonResponse<Map> check(@RequestBody @Valid UserVo userVO){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userVO.getCode(),userVO.getPassword());
        subject.login(token);
        UserDto userDto = CommonUtils.copyProperties(userVO,UserDto.class);
        Map<String,Object> data = loginService.createToken(userDto);
        return new CommonResponse<>(state.SUCCESS,state.SUCCESS_MSG,data);

    }

    @PostMapping(value = "/getInfo")
    public CommonResponse<UserInfo> getInfo(@RequestBody CommonRequest request){
        String token = request.getToken();
        return new CommonResponse<>(state.SUCCESS,state.SUCCESS_MSG,loginService.getUserInfo(token));
    }

    @PostMapping(value = "/getMenu")
    public CommonResponse<List> getMenu(@RequestBody CommonRequest request){
        String token = request.getToken();
        return new CommonResponse<>(state.SUCCESS,state.SUCCESS_MSG,loginService.getUserMenu(token));
    }

    @PostMapping(value = "/logout")
    public CommonResponse<Boolean> logout(@RequestBody CommonRequest<List<Long>> commonRequest) throws Exception {
        List<Long> ids = commonRequest.getData();
        return new CommonResponse<>(state.SUCCESS,state.SUCCESS_MSG,loginService.logout(ids));
    }
}
