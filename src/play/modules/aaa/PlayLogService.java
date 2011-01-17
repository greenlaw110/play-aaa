package play.modules.aaa;

import play.Logger;

public class PlayLogService implements ILogService {
    
    public static ILogService instance = new PlayLogService();
    public static ILogService get() {
        return instance;
    }

    @Override
    public void log(IAccount principal, boolean autoAck, String level,
            String message, Object... args) {
        level = null == level ? "info" : level.trim().toLowerCase();
        if ("info".equals(level)) {
            Logger.info(message, args);
        } else if ("warn".equals(level)) {
            Logger.warn(message, args);
        } else if ("error".equals(level)) {
            Logger.error(message, args);
        } else if ("fatal".equals(level)) {
            Logger.fatal(message, args);
        } else if ("debug".equals(level)) {
            Logger.debug(message, args);
        } else if ("trace".equals(level)) {
            Logger.trace(message, args);
        } else {
            Logger.info(message, args);
        }
    }

}
