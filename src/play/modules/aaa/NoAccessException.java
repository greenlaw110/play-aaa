package play.modules.aaa;

import play.Logger;
import play.utils.FastRuntimeException;

public class NoAccessException extends FastRuntimeException {

    private static final long serialVersionUID = 1080270792010078044L;

    public NoAccessException() {
    }

    public NoAccessException(Throwable cause) {
        super(cause.getMessage());
        Logger.warn(cause, "Access denied");
    }

    public NoAccessException(String message) {
        super(message);
    }

    public NoAccessException(String message, Throwable cause) {
        super(message);
        Logger.warn(cause, "Access denied: %s", message);
    }

}
