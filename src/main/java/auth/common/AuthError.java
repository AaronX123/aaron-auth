package auth.common;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-15
 */
public enum AuthError {
    USER_NOT_EXIST("040001","用户不存在"),
    ONLINE_INSERT_FAIL("040002","上线状态保存失败"),
    LOGIN_FAIL("040003","用户名或密码错误"),
    ;
    private String msg;
    private String code;

    AuthError(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
