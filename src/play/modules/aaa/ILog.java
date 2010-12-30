package play.modules.aaa;

import com.google.gson.JsonSerializer;

public interface ILog {

    Object getId();
    
    String getLevel();

    String getMessage();

    long getTimestamp();

    boolean acknowledged();

    void acknowledge();

    IAccount getPrincipal();

    IAccount getAcknowledger();

    /**
     * Calling this method shall be the same effect of calling
     * <code>log(account, ...)</code> with account set to
     * {@link IAccount#getCurrent()} and <code>autoAck</code> set to
     * <code>false</code>
     * 
     * @param level
     * @param message
     * @param args
     */
    void log(String message, String level, Object... args);

    /**
     * Calling this method shall be the same effect of calling
     * <code>log(account, ...)</code> with account set to
     * {@link IAccount#getCurrent()}
     * 
     * @param level
     * @param message
     * @param args
     */
    void log(String message, boolean autoAck, String level, Object... args);

    /**
     * Factory method to add one log item for accounting purpose. This method
     * shall automatically set timestamp to {@link System#currentTimeMillis()}.
     * 
     * <p>
     * If autoAck is set to true then the log entry will be acknowledged
     * 
     * @param principal
     * @boolean autoAck
     * @param level
     * @param message
     * @param args
     */
    void log(IAccount principal, boolean autoAck, String level, String message,
            Object... args);

    /**
     * Factory method to return JsonSerializer for this type
     * @return
     */
    JsonSerializer getJsonSerializer();
}
