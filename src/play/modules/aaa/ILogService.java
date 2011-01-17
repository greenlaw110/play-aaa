package play.modules.aaa;

public interface ILogService {
    void log(IAccount principal, boolean autoAck, String level, String message, Object... args);
}
