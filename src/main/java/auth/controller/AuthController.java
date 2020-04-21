package auth.controller;

import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.logging.annotation.MethodEnhancer;
import aaron.common.utils.CommonUtils;
import auth.common.AuthError;
import auth.common.AuthException;
import auth.pojo.dto.UserDto;
import auth.pojo.model.UserInfo;
import auth.pojo.vo.UserVo;
import auth.service.LoginService;
import com.alibaba.fastjson.JSON;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author xym
 * @version V1.0.0
 * @date 2019/8/26
 */
@RestController
@CrossOrigin(allowedHeaders = "*",allowCredentials = "true",methods = {})
public class AuthController {
    @Autowired
    private LoginService loginService;

    @Autowired
    CommonState state;

    @MethodEnhancer
    @PostMapping(value = "/login")
    public CommonResponse<Map> check(@RequestBody @Valid UserVo userVO){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userVO.getCode(),userVO.getPassword());
        try {
            subject.login(token);
        }catch (Exception e){
            throw new AuthException(AuthError.LOGIN_FAIL);
        }
        UserDto userDto = CommonUtils.copyProperties(userVO,UserDto.class);
        Map<String,Object> data = loginService.createToken(userDto);
        return new CommonResponse<>(state.SUCCESS,state.SUCCESS_MSG,data);

    }

    @MethodEnhancer
    @PostMapping(value = "/getInfo")
    public CommonResponse<UserInfo> getInfo(@RequestBody CommonRequest request){
        String token = request.getToken();
        return new CommonResponse<>(state.SUCCESS,state.SUCCESS_MSG,loginService.getUserInfo(token));
    }

    @MethodEnhancer
    @PostMapping(value = "/getMenu")
    public CommonResponse<List> getMenu(@RequestBody CommonRequest request){
        String token = request.getToken();
        List list = loginService.getUserMenu(token);
        return new CommonResponse<>(state.SUCCESS,state.SUCCESS_MSG,list);
    }

    @MethodEnhancer
    @RequestMapping(value = "/logout")
    public CommonResponse<Boolean> logout(@RequestBody CommonRequest<List<Long>> commonRequest) throws Exception {
        List<Long> ids = commonRequest.getData();
        return new CommonResponse<>(state.SUCCESS,state.SUCCESS_MSG,loginService.logout(ids));
    }

}
