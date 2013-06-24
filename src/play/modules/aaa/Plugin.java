package play.modules.aaa;

import org.osgl.IContextInitializer;
import org.osgl.play.JobContext;
import org.yaml.snakeyaml.Yaml;
import play.Logger;
import play.Play;
import play.Play.Mode;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.db.jpa.JPAPlugin;
import play.exceptions.ConfigurationException;
import play.exceptions.UnexpectedException;
import play.libs.IO;
import play.modules.aaa.PlayDynamicRightChecker.IAccessChecker;
import play.modules.aaa.enhancer.Enhancer;
import play.modules.aaa.utils.AAA;
import play.modules.aaa.utils.AAAFactory;
import play.modules.aaa.utils.ConfigConstants;
import play.modules.aaa.utils.ConfigurationAuthenticator;
import play.mvc.Scope.Session;
import play.vfs.VirtualFile;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <code>play.module.aaa.Plugin</code> provides off the shelf framework for
 * Authentication/Authorization/Accounting facilities for Play application
 * development. These includes
 * <p/>
 * <ul>
 * <li>Interfaces and default implementations in JPA and MongoDB (require
 * {@link //www.playframework.org/modules/morphia morphia plugin}</li>
 * <li>Annotations and byte code enhancements for declarative privilege,
 * rights and accounting on application methods</li>
 * <li>Admin Web UI to manage user accounts/rights/privilege</li>
 * </ul>
 *
 * @author greenlaw110@gmail.com
 * @version 1.0 23/12/2010
 */
public class Plugin extends PlayPlugin implements ConfigConstants {
    public static final String VERSION = "1.6e";

    private static String msg_(String msg, Object... args) {
        return String.format("AAAPlugin-" + VERSION + "> %1$s",
                String.format(msg, args));
    }

    public static void info(String msg, Object... args) {
        Logger.info(msg_(msg, args));
    }

    public static void debug(String msg, Object... args) {
        Logger.debug(msg_(msg, args));
    }

    public static void trace(String msg, Object... args) {
        Logger.trace(msg_(msg, args));
    }

    private static Enhancer e_ = new Enhancer();
    private Set<String> noAuthenticated = new HashSet<String>();

    public void enhance(ApplicationClass applicationClass) throws Exception {
        e_.enhanceThisClass(applicationClass);
    }

    public static void buildAuthRegistry() throws Exception {
        e_.buildAuthorityRegistry();
    }

    @Override
    public void beforeActionInvocation(Method actionMethod) {
        String name = Session.current().get("username");
        IAccount account = AAAFactory.account().getByName(name);
        if (null != account) AAA.currentAccount(account);
        else Session.current().remove("username");
    }

    @Override
    public void afterInvocation() {
        AAA.clearContext();
    }

    //   private boolean noAccess_(Throwable t) {
//       boolean b = t instanceof NoAccessException;
//       if (b && null == t.getCause()) return b;
//       return noAccess_(t.getCause());
//   }
//
//   @Override
//   public void onInvocationException(Throwable t) {
//       if (noAccess_(t)) {
//           throw new Forbidden(t.getMessage());
//       }
//   }

    public static class DDL {
        private boolean create;
        private boolean update;
        private boolean delete;
        public boolean create() {return create;}
        public boolean update() {return update;}
        public boolean delete() {return delete;}

        DDL(boolean c, boolean u, boolean d) {
            create = c;
            update = u;
            delete = d;
        }

        public static DDL get() {
            String s = Play.configuration.getProperty("aaa.ddl");
            if (s == null) {
                return Play.mode.isDev()? new DDL(true, true, false) : new DDL(true, false, false);
            } else {
                boolean c = s.indexOf("create") != -1;
                boolean u = s.indexOf("update") != -1;
                boolean d = s.indexOf("delete") != -1;
                return new DDL(c, u, d);
            }
        }
    }

    @Override
    public void onConfigurationRead() {
        if (isJPAModel_()) {
            String jpaEntities = Play.configuration.getProperty("jpa.entities", "").trim();
            if (!"".equals(jpaEntities)) {
                jpaEntities += ",play.modules.aaa.Account";
            } else {
                jpaEntities = "play.modules.aaa.Account";
            }
            Play.configuration.put("jpa.entities", jpaEntities);
        }

        String s = Play.configuration.getProperty("aaa.superuser");
        if (null != s) {
            try {
                int i = Integer.valueOf(s);
                superuser = i;
            } catch (Exception e) {
                Logger.warn(msg_("Error parsing configuration aaa.superuser. Please use positive integer."));
            }
        }
        if (superuser <= 0)
            Logger.info(msg_("superuser privilege disabled"));
        else
            Logger.info(msg_("superuser privilege: %s", superuser));
    }

    public static int superuser = 9999;

    public static boolean logCheckTime = false;

    @Override
    public void onApplicationStart() {
        instance_ = this;
        debug("onApplicationStart");

        logCheckTime = Boolean.parseBoolean(Play.configuration.getProperty("aaa.logCheckTime", "false"));
        load_();
        if (Play.mode == Mode.DEV && Boolean.parseBoolean(Play.configuration.getProperty(DISABLE, "false"))) {
            Logger.warn("AAA disabled in Dev mode");
        } else {
            /*
             * enable user to explicitly build auth registry by setting aaa.buildAuthRegistry to false
             */
            if (Boolean.parseBoolean(Play.configuration.getProperty(BUILD_AUTH_REGISTRY, "true")))
                try {
                    e_.buildAuthorityRegistry();
                } catch (Exception e) {
                    throw new UnexpectedException(e);
                }
        }
        new ConfigurationAuthenticator.BootLoader().doJob();
        List<Class> cl = Play.classloader.getAssignableClasses(IAccessChecker.class);
        for (Class c : cl) {
            try {
                PlayDynamicRightChecker.registerChecker((IAccessChecker) c.newInstance());
            } catch (Exception e) {
                throw new UnexpectedException(e);
            }
        }
        AAA._loadSuperUser();
        JobContext.registerInitializer(new IContextInitializer() {
            @Override
            public void initContext() {
                AAA.initContext();
            }
        });
        Logger.info(msg_("initialized"));
    }

    /*
     * return the first file found by a set of names specified
     */
    private static VirtualFile virtualFile_(String... fileNames) {
        if (fileNames.length == 0) return null;
        for (String fn : fileNames) {
            VirtualFile vf = VirtualFile.search(Play.javaPath, fn);
            if (null != vf) return vf;
        }
        return null;
    }

    private static final Pattern P_Account = Pattern.compile("(Account|Acc|A)", Pattern.CASE_INSENSITIVE);
    private static final Pattern P_Role = Pattern.compile("(Role|RO)", Pattern.CASE_INSENSITIVE);
    private static final Pattern P_Privilege = Pattern.compile("(Privilege|Priv|P)", Pattern.CASE_INSENSITIVE);
    private static final Pattern P_Right = Pattern.compile("(Right|RI)", Pattern.CASE_INSENSITIVE);

    private static void load_(VirtualFile yamlFile) {
        Logger.info(msg_("loading aaa configuration from yaml file: %s", yamlFile.relativePath()));
        String s = IO.readContentAsString(yamlFile.getRealFile());
        Yaml yaml = new Yaml();
        IAccount accFact = AAAFactory.account();
        IRole rolFact = AAAFactory.role();
        IPrivilege priFact = AAAFactory.privilege();
        IRight rigFact = AAAFactory.right();
        DDL ddl = DDL.get();
        try {
            startTx_();
            if (ddl.delete()) {
                accFact._deleteAll();
                rolFact._deleteAll();
                rigFact._deleteAll();
                priFact._deleteAll();
            }
            Object o = yaml.load(s);
            if (o instanceof LinkedHashMap<?, ?>) {
                Map<String, IAccount> accounts = new HashMap<String, IAccount>();
                Map<String, IRole> roles = new HashMap<String, IRole>();
                Map<String, IRight> rights = new HashMap<String, IRight>();
                Map<String, IPrivilege> privileges = new HashMap<String, IPrivilege>();
                @SuppressWarnings("unchecked")
                Map<Object, Map<?, ?>> objects = (Map<Object, Map<?, ?>>) o;
                for (Object key : objects.keySet()) {
                    String name = key.toString().trim();
                    Map<?, ?> mm = objects.get(key);
                    String type = (String) mm.get("type");
                    if (null == type) type = "account"; // default item type is account
                    if (P_Account.matcher(type).matches()) {
                        IAccount acc = accFact.getByName(name);
                        if (null != acc) {
                            if (!ddl.update()) continue;
                        } else {
                            if (!ddl.create()) continue;
                            acc = accFact.create(name);
                        }
                        String password = (String)mm.get("password");
                        if (null != password && !"".equals(password.trim())) {
                            acc.setPassword(password);
                        }
                        // privilege for the account
                        acc.revokePrivilege();
                        s = (String)mm.get("privilege");
                        if (null != s) {
                            IPrivilege p = privileges.get(s);
                            if (null == p) throw new ConfigurationException("Cannot find privilege [" + s + "] when loading account [" + name + "]");
                            acc.assignPrivilege(p);
                        }
                        // roles for the account
                        acc.revokeRole(acc.getRoles());
                        List<String> sl = (List<String>) mm.get("roles");
                        if (null != sl) {
                            for (String s0 :  sl) {
                                IRole r = roles.get(s0);
                                if (null == r) throw new ConfigurationException("Cannot find role [" + s0 + "] when loading account [" + name + "]");
                                acc.assignRole(r);
                            }
                        }
                        accounts.put(name, acc);
                    } else if (P_Privilege.matcher(type).matches()) {
                        IPrivilege pri = priFact.getByName(name);
                        if (null != pri) {
                            if (!ddl.update()) {
                                privileges.put(name, pri);
                                continue;
                            }
                            int lvl = (Integer)mm.get("level");
                            if (pri.getLevel() != lvl) {
                                pri._delete();
                                pri = priFact.create(name, lvl);
                            }
                            privileges.put(name, pri);
                        } else {
                            if (!ddl.create()) continue;
                            int lvl = (Integer)mm.get("level");
                            pri = priFact.create(name, lvl);
                            privileges.put(name, pri);
                        }
                    } else if (P_Right.matcher(type).matches()) {
                        IRight right = rigFact.getByName(name);
                        if (null != right) {
                            if (ddl.update()) {
                                boolean dyna = mm.containsKey("dynamic") ? (Boolean) mm.get("dynamic") : false;
                                right.setDynamic(dyna);
                            }
                            rights.put(name, right);
                        } else {
                            if (!ddl.create()) continue;
                            right = rigFact.create(name);
                            boolean dyna = mm.containsKey("dynamic") ? (Boolean) mm.get("dynamic") : false;
                            right.setDynamic(dyna);
                            rights.put(name, right);
                        }
                    } else if (P_Role.matcher(type).matches()) {
                        IRole role = rolFact.getByName(name);
                        if (null != role) {
                            if (!ddl.update()) {
                                roles.put(name, role);
                                continue;
                            }
                            role.removeRights(role.getRights());
                        } else {
                            role = rolFact.create(name);
                        }
                        List<String> sl = (List<String>)mm.get("rights");
                        if (null == sl) throw new ConfigurationException("No rights configured for role [" + name + "]");
                        for (String s0: sl) {
                            IRight r = rights.get(s0);
                            if (null == r) throw new ConfigurationException("Cannot find right [" + s0 + "] when loading role [" + name + "]");
                            role.addRight(r);
                        }
                        roles.put(name, role);
                    } else {
                        throw new ConfigurationException("unknown aaa object type[" + type + "] found when loading object [" + name + "]");
                    }
                }
                for (IPrivilege p: privileges.values()) {
                    p._save();
                }
                for (IRight i: rights.values()) {
                    i._save();
                }
                for (IRole r: roles.values()) {
                    r._save();
                }
                for (IAccount a: accounts.values()) {
                    a._save();
                }
            } else {
                throw new RuntimeException("aaa yml format not recognized: " + yamlFile.relativePath());
            }
            commitTx_();
        } catch (Exception e) {
            rollbackTx_();
            throw new RuntimeException(e);
        }
    }

    private void load_() {
        String fileName = Play.configuration.getProperty("aaa.yamlFile", "_aaa.yml");
        VirtualFile yamlFile = virtualFile_(fileName);

        if (yamlFile == null) {
            Logger.warn(msg_("Couldn't find aaa plugin initial file: %s", fileName));
            return;
        }
        load_(yamlFile);
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
