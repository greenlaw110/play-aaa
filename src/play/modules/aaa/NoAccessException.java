package play.modules.aaa;

import play.Logger;

public class NoAccessException extends play.mvc.results.Forbidden {

    private static final long serialVersionUID = 1080270792010078044L;

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
