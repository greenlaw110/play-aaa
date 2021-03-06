package play.modules.aaa.utils;

import play.Logger;
import play.modules.aaa.IAccount;

public class Accounting {

    public static void log(boolean allowSystem, boolean autoAck, String level,
            String action) {
        log(allowSystem, autoAck, level, action, new Object[] {});
    }

    public static void log(boolean allowSystem, boolean autoAck, String level,
            String action, Object... args) {
        IAccount acc = null;
        try {
            IAccount accFact = AAAFactory.account();
            acc = accFact.getCurrent();
            if (null == acc && allowSystem) {
                acc = accFact.getSystemAccount();
            }
        } catch (Exception e) {
            Logger.error(e, "Error get principal");
        }
        try {
            AAAFactory.log().log(acc, autoAck, level, action, args);
        } catch (Exception e) {
            Logger.error(e, "error get Log implementation");
        }
    }

    public static void abort(String reason, String action, boolean allowSystem,
            Object... args) {
        if (args.length > 0) {
            action = String.format(action, args);
        }
        String msg = String.format("%1$s aborted: %2$s", action, reason);
        log(allowSystem, false, "abort", msg);
    }

    public static void error(String reason, String action, boolean allowSystem) {
        error(reason, action, allowSystem, new Object[] {});
    }

    public static void error(String reason, String action, boolean allowSystem,
            Object... args) {
        if (args.length > 0) {
            action = String.format(action, args);
        }
        action = formatErrMsg_(reason, action);
        log(allowSystem, false, "error", action);
    }

    public static void error(Exception e, String action, boolean allowSystem) {
        error(e, action, allowSystem, new Object[] {});
    }

    public static void error(Exception e, String action, boolean allowSystem,
            Object... args) {
        if (args.length > 0) {
            action = String.format(action, args);
        }
        action = formatErrMsg_(e, action);
        log(allowSystem, false, "error", action);
    }

    // for enhancer
    public static void error(Exception e, String action, boolean allowSystem,
            Object s1, Object s2) {
        action = String.format(action, s1, s2);
        action = formatErrMsg_(e, action);
        log(allowSystem, false, "error", action);
    }

    // for enhancer
    public static void error(Exception e, String action, boolean allowSystem,
            Object s1, Object s2, Object s3) {
        action = String.format(action, s1, s2, s3);
        action = formatErrMsg_(e, action);
        log(allowSystem, false, "error", action);
    }

    // for enhancer
    public static void error(Exception e, String action, boolean allowSystem,
            Object s1, Object s2, Object s3, Object s4) {
        action = String.format(action, s1, s2, s3, s4);
        action = formatErrMsg_(e, action);
        log(allowSystem, false, "error", action);
    }

    public static void fatal(String reason, String action, boolean allowSystem) {
        fatal(reason, action, allowSystem, new Object[] {});
    }

    public static void fatal(String reason, String action, boolean allowSystem,
            Object... args) {
        if (args.length > 0) {
            action = String.format(action, args);
        }
        action = formatErrMsg_(reason, action);
        log(allowSystem, false, "fatal", action);
    }

    public static void fatal(Exception e, String action, boolean allowSystem) {
        fatal(e, action, allowSystem, new Object[] {});
    }

    public static void fatal(Exception e, String action, boolean allowSystem,
            Object... args) {
        if (args.length > 0) {
            action = String.format(action, args);
        }
        action = formatErrMsg_(e, action);
        log(allowSystem, false, "fatal", action);
    }

    public static void warn(String reason, String action, boolean allowSystem) {
        warn(reason, action, allowSystem, new Object[] {});
    }

    public static void warn(String reason, String action, boolean allowSystem,
            Object... args) {
        if (args.length > 0) {
            action = String.format(action, args);
        }
        action = formatErrMsg_(reason, action);
        log(allowSystem, false, "warn", action);
    }

    public static void warn(Exception e, String action, boolean allowSystem) {
        warn(e, action, allowSystem, new Object[] {});
    }

    public static void warn(Exception e, String action, boolean allowSystem,
            Object... args) {
        if (args.length > 0 && null != action) {
            action = String.format(action, args);
        }
        action = formatErrMsg_(e, action);
        log(allowSystem, false, "warn", action);
    }

    public static void info(String msg, boolean allowSystem) {
        info(msg, allowSystem, new Object[] {});
    }

    public static void info(String msg, boolean allowSystem, Object... args) {
        log(allowSystem, true, "info", msg, args);
    }

    public static void info(String msg, boolean allowSystem, String str1,
            String str2) {
        log(allowSystem, true, "info", msg, str1, str2);
    }

    public static void info(String msg, boolean allowSystem, String s1,
            String s2, String s3) {
        log(allowSystem, true, "info", msg, s1, s2, s3);
    }

    public static void info(String msg, boolean allowSystem, String s1,
            String s2, String s3, String s4) {
        log(allowSystem, true, "info", msg, s1, s2, s3, s4);
    }

    private static String formatErrMsg_(String reason, String action) {
        if (null == action || null == reason) {
            Logger.error(new NullPointerException(),
                    "Invalid reason[%1$s] or action[%2$s] found!", reason,
                    action);
        }
        return String.format("Error encountered during executing [%1$s]: %2$s",
                action, reason);
    }

    private static String formatErrMsg_(Exception e, String action) {
        if (null == action || null == e) {
            Logger.error(new NullPointerException(),
                    "Invalid reason[%1$s] or action[%2$s] found!", e, action);
        }
        return String.format("Error encountered during executing [%1$s]: %2$s",
                action, null == e ? "unknown error" : e);
    }

}
