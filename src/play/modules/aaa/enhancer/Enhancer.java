package play.modules.aaa.enhancer;

import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import play.Play;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.modules.aaa.AllowSystemAccount;
import play.modules.aaa.IAccount;
import play.modules.aaa.IAuthorizeable;
import play.modules.aaa.IPrivilege;
import play.modules.aaa.IRight;
import play.modules.aaa.NoAccessException;
import play.modules.aaa.RequireAccounting;
import play.modules.aaa.RequirePrivilege;
import play.modules.aaa.RequireRight;
import play.modules.aaa.utils.AnnotationHelper;
import play.modules.aaa.utils.ConfigConstants;
import play.modules.aaa.utils.Factory;

public class Enhancer extends play.classloading.enhancers.Enhancer {

   @Override
   public void enhanceThisClass(ApplicationClass applicationClass)
         throws Exception {
      enhance_(applicationClass, false);
   }

   private void enhance_(ApplicationClass applicationClass,
         boolean buildAuthorityRegistryOnly) throws Exception {
      CtClass ctClass = makeClass(applicationClass);
      for (final CtMethod ctMethod : ctClass.getDeclaredMethods()) {
         if (!Modifier.isPublic(ctMethod.getModifiers())) {
            continue;
         }

         boolean needsEnhance = false;
         RequireRight rr = null;
         RequirePrivilege rp = null;
         RequireAccounting ra = null;
         boolean allowSystem = false;
         Object[] aa = ctMethod.getAnnotations();
         for (Object o : aa) {
            if (o instanceof RequirePrivilege) {
               needsEnhance = true;
               rp = (RequirePrivilege) o;
               continue;
            }
            if (o instanceof RequireRight) {
               needsEnhance = true;
               rr = (RequireRight) o;
               continue;
            }
            if (o instanceof AllowSystemAccount) {
               allowSystem = true;
               continue;
            }
            if (o instanceof RequireAccounting) {
               needsEnhance = true;
               ra = (RequireAccounting) o;
            }
         }
         if (!needsEnhance)
            return;

         String key = ctMethod.getLongName();
         IRight r = rr == null ? null : AnnotationHelper.getRequiredRight(rr,
               key);
         IPrivilege p = rp == null ? null : AnnotationHelper
               .getRequiredPrivilege(rp, key);

         if (r == null && p == null) {
            throw new RuntimeException(
                  "Invalid AAA annotation found: neither privilege nor right could be identified");
         }
         Authority.registAuthoriable(key, r, p);
         if (!buildAuthorityRegistryOnly) {
            ctMethod
                  .insertBefore("play.modules.aaa.enhancer.Enhancer.Authority.checkPermission(\""
                        + key + "\", " + Boolean.toString(allowSystem) + ");");
            if (null != ra) {
               String msg = ra.value();
               if (null == msg || "".equals(msg))
                  msg = key;
               ctMethod
                     .insertBefore("play.modules.aaa.utils.Accounting.info(\""
                           + msg + "\", " + Boolean.toString(allowSystem)
                           + ", $$);");
               CtClass etype = ClassPool.getDefault()
                     .get("java.lang.Exception");
               ctMethod.addCatch(
                     "{play.modules.aaa.utils.Accounting.error($e, \"" + msg
                           + "\", " + Boolean.toString(allowSystem)
                           + ", $$); throw $e;}", etype);
            }
         }
      }
      if (!buildAuthorityRegistryOnly) {
         applicationClass.enhancedByteCode = ctClass.toBytecode();
         ctClass.detach();
      }
   }

   public void buildAuthorityRegistry() throws Exception {
      if (Authority.reg_.size() > 0)
         return; // suppose it's already built up during code enhancement

      for (ApplicationClass ac : Play.classes.all()) {
         enhance_(ac, true);
      }
   }

   public static class Authority implements IAuthorizeable {

      private IRight r_ = null;
      private IPrivilege p_ = null;

      private Authority(IRight r, IPrivilege p) {
         if (null == r && null == p) {
            throw new IllegalArgumentException(
                  "at least one of right and privilege should be non-null value");
         }
         r_ = r;
         p_ = p;
      }

      @Override
      public IRight getRequiredRight() {
         return r_;
      }

      @Override
      public IPrivilege getRequiredPrivilege() {
         return p_;
      }

      private static final Map<String, IAuthorizeable> reg_ = new HashMap<String, IAuthorizeable>();

      private static void registAuthoriable(String key, IRight r, IPrivilege p) {
         reg_.put(key, new Authority(r, p));
      }

      public static IAuthorizeable getRight(String key) {
         return reg_.get(key);
      }

      public static void checkPermission(String key, boolean allowSystem)
            throws NoAccessException {
         if (Play.mode == Play.Mode.DEV) {
            if (Boolean.parseBoolean(Play.configuration.getProperty(
                  ConfigConstants.DISABLE, "false"))) {
               return;
            }
         }

         IAuthorizeable a = reg_.get(key);
         if (null == a) {
            throw new RuntimeException(
                  "oops, something wrong with enhancer... ?");
         }
         IAccount acc = null;
         try {
            IAccount accFact = Factory.account();
            acc = accFact.getCurrent();
            if (null == acc) {
               if (allowSystem) {
                  if (!Boolean.parseBoolean(Play.configuration.getProperty(ConfigConstants.SYSTEM_PERMISSION_CHECK, "false"))) {
                     // suppress permission check for system account
                     return;
                  }
                  acc = accFact.getSystemAccount();
               }
               if (null == acc) {
                  throw new NoAccessException(
                        "cannot determine principal account");
               }
            }

            if (!acc.hasAccessTo(a)) {
               throw new NoAccessException("no permission");
            }
         } catch (NoAccessException nae) {
            throw nae;
         } catch (Exception e) {
            throw new NoAccessException(e);
         }

      }
   }
}
