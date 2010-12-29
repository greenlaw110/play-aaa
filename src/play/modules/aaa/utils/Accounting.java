package play.modules.aaa.utils;

import play.Logger;
import play.modules.aaa.IAccount;

public class Accounting {

	public static void log(boolean allowSystem, boolean autoAck, String level, String msg) {
        log(allowSystem, autoAck, level, msg, new Object[]{});
     }
     
     public static void log(boolean allowSystem, boolean autoAck, String level, String msg, Object... args) {
        IAccount acc = null;
        try {
           IAccount accFact = Factory.account();
           acc = accFact.getCurrent();
           if (null == acc && allowSystem) {
              acc = accFact.getSystemAccount();
           }
        } catch (Exception e) {
           Logger.error(e, "Error get principal");
        }
        if (args.length > 0) {
           msg = String.format("Executing [%1$s] with arguments: %1$s", msg, args);
        }
        try {
           Factory.log().log(acc, autoAck, level, msg, args);
        } catch (Exception e) {
           Logger.error(e, "error get Log implementation");
        }
     }

     public static void error(Exception e, String msg, boolean allowSystem) {
         error(e, msg, allowSystem, new Object[]{});
      }
      
      public static void error(Exception e, String msg, boolean allowSystem, Object... args) {
         msg = String.format("Error encountered during executing [%1$s]: %2$s", msg, e.getMessage());
         log(allowSystem, false, "error", msg, args);
      }
      
      public static void warn(Exception e, String msg, boolean allowSystem) {
          warn(e, msg, allowSystem, new Object[]{});
       }
       
       public static void warn(Exception e, String msg, boolean allowSystem, Object... args) {
          msg = String.format("Error encountered during executing [%1$s]: %2$s", msg, e.getMessage());
          log(allowSystem, false, "warn", msg, args);
       }
       
       public static void info(String msg, boolean allowSystem) {
           info(msg, allowSystem, new Object[]{});
        }
        
        public static void info(String msg, boolean allowSystem, Object... args) {
           log(allowSystem, true, "info", msg, args);
        }
        
}
