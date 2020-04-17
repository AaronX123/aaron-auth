package auth.common;

import aaron.common.data.exception.NestedExamException;
import aaron.common.data.exception.StarterError;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-15
 */
public class AuthException extends NestedExamException {

    public AuthException(String errorMessage, String errorCode) {
        super(errorMessage, errorCode);
    }

    public AuthException(AuthError error){
        super(error.getMsg(),error.getCode());
    }

    public AuthException(StarterError error){
        super(error.getMsg(),error.getCode());
    }
}
