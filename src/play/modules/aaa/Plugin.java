package play.modules.aaa;

import play.Logger;
import play.Play;
import play.Play.Mode;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.db.jpa.JPAPlugin;
import play.exceptions.UnexpectedException;
import play.modules.aaa.enhancer.Enhancer;
import play.modules.aaa.utils.ConfigConstants;
import play.modules.aaa.utils.ConfigurationAuthenticator;

/**
 * <code>play.module.aaa.Plugin</code> provides off the shelf framework for
 * Authentication/Authorization/Accounting facilities for Play application
 * development. These includes
 * 
 * <ul>
 * <li>Interfaces and default implementations in JPA and MongoDB (require
 * {@link http://www.playframework.org/modules/morphia morphia plugin}</li>
 * <li>Annotations and byte code enhancements for declarative privilege,
 * rights and accounting on application methods</li>
 * <li>Admin Web UI to manage user accounts/rights/privilege</li>
 * </ul>
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0 23/12/2010
 */
public class Plugin extends PlayPlugin implements ConfigConstants {
   public static final String VERSION = "1.0";

   private static String msg_(String msg) {
      return String.format("AAAPlugin-" + VERSION + "> %1$s", msg);
   }

   private static Enhancer e_ = new Enhancer();
   public void enhance(ApplicationClass applicationClass) throws Exception {
      e_.enhanceThisClass(applicationClass);
   }
   
   @Override
   public void onApplicationStart() {
      instance_ = this;
      if (Play.mode == Mode.DEV && Boolean.parseBoolean(Play.configuration.getProperty(DISABLE, "false"))) {
         Logger.warn("AAA disabled in Dev mode");
      } else {
    	  try {
    		  e_.buildAuthorityRegistry();
    	  } catch (Exception e) {
    		  throw new UnexpectedException(e);
    	  }
      }
      new ConfigurationAuthenticator.BootLoader().doJob();
      Logger.info(msg_("initialized"));
   }

   private static boolean isJPAModel_() {
      return AAA_IMPL_JPA.equals(Play.configuration.getProperty(AAA_IMPL));
   }
   
   private static void startTx_() {
      if (isJPAModel_()) {
         JPAPlugin.startTx(false);
      }
   }
   
   private static void commitTx_() {
      if (isJPAModel_()) {
         JPAPlugin.closeTx(false);
      }
   }
   
   private static void rollbackTx_() {
      if (isJPAModel_()) {
         JPAPlugin.closeTx(true);
      }
   }

   private static Plugin instance_ = null;

   public static Plugin instance() {
      return instance_;
   }
}
