package play.modules.aaa.utils;

import com.greenlaw110.play.JobContext;
import play.Play;
import play.modules.aaa.*;

import java.util.Stack;

public class AAA {

    private static IPrivilege PRV_SUPERUSER;

    public static void _loadSuperUser() {
        String su_name = Play.configuration.getProperty(ConfigConstants.SUPERUSER, "superuser");
        PRV_SUPERUSER = getPrivilege(su_name);
    }

    private static final String CUR_ACC = "aaa.me";
    private static final String TGT_RSRC = "aaa.tgt";
    private static final String ALLOW_SYSTEM = "aaa.allowSystem";

    public static void initContext() {
        JobContext.put(TGT_RSRC, new Stack<Object>());
    }

    public static void clearContext() {
        JobContext.remove(CUR_ACC);
        Stack<Object> s = JobContext.get(TGT_RSRC, Stack.class);
        if (null != s) {
            s.clear();
            JobContext.remove(TGT_RSRC);
        }
    }
    
    public static void setSystemAccount() {
        JobContext.put(CUR_ACC, getAccount("root"));
    }

    /**
     * Alias of currentAccount()
     * @return
     */
    public static IAccount me() {
        return JobContext.get(CUR_ACC, IAccount.class);
    }

    public static IAccount currentAccount() {
        return me();
    }

    public static void currentAccount(IAccount account) {
        JobContext.put(CUR_ACC, account);
    }

    public static void pushTargetResource(Object resource) {
        Stack<Object> s = JobContext.get(TGT_RSRC, Stack.class);
        s.push(resource);
    }

    public static Object targetResource() {
        Stack<Object> s = JobContext.get(TGT_RSRC, Stack.class);
        return s.peek();
    }

    public static Object popTargetResource() {
        Stack<Object> s = JobContext.get(TGT_RSRC, Stack.class);
        return s.pop();
    }

    public static boolean allowSystem() {
        Boolean allowSystem = JobContext.get(ALLOW_SYSTEM, Boolean.class);
        return null == allowSystem ? false : allowSystem;
    }

    public static void allowSystem(boolean allow) {
        JobContext.put(ALLOW_SYSTEM, true);
    }

    public static boolean isSuperUser() {
        return isSuperUser(me());
    }

    public static IAccount systemAccount() {
        return AAAFactory.account().getSystemAccount();
    }

    public static boolean isSuperUser(String username) {
        IAccount acc = getAccount(username);
        return null == acc ? false : isSuperUser(acc);
    }

    public static boolean isSuperUser(IAccount account) {
        if (null == account) return false;
        IPrivilege p = account.getPrivilege();
        if (null == p) return false;
        return PRV_SUPERUSER.compareTo(p) <= 0;
    }

    private static boolean setPassword(IAccount acc, String password) {
        acc.setPassword(password);
        acc._save();
        return true;
    }

    private static boolean setPassword(IAccount acc, String password, String oldPassword) {
        acc = acc.authenticate(acc.getName(), oldPassword);
        if (null == acc) return false;
        setPassword(acc, password);
        return true;
    }

    public static boolean setMyPassword(String password) {
        IAccount me = currentAccount();
        if (null == me) return false;
        return setPassword(me, password);
    }

    public static boolean setMyPassword(String password, String oldPassword) {
        IAccount me = currentAccount();
        if (null == me) return false;
        return setPassword(me, password, oldPassword);
    }

    public static boolean setPassword(String username, String password) {
        IAccount acc = AAA.getAccount(username);
        if (null == acc) return false;
        return setPassword(acc, password);
    }

    public static boolean setPassword(String username, String password, String oldPassword) {
        IAccount acc = AAA.getAccount(username);
        if (null == acc) return false;
        return setPassword(acc, password, oldPassword);
    }

    public static boolean hasRole(String... roles) {
        return hasRole(currentAccount(), roles);
    }

    public static boolean hasRole(IAccount account, String... roles) {
        if (null == account) return false;
        for (String role: roles) {
            IRole r = getRole(role);
            if (null == r) continue;
            if (account.getRoles().contains(r)) return true;
        }
        return false;
    }

    public static boolean hasRole(IAccount account, IRole... roles) {
        if (null == account) return false;
        for (IRole r: roles) {
            if (account.getRoles().contains(r)) return true;
        }
        return false;
    }

    public static IAccount getAccount(String name) {
        return AAAFactory.account().getByName(name);
    }

    public static IAccount getOrCreateAccount(String name) {
        IAccount acc = getAccount(name);
        if (null == acc) return AAAFactory.account().create(name);
        else return acc;
    }

    public static IRole getRole(String name) {
        return AAAFactory.role().getByName(name);
    }

    public static IRight getRight(String name) {
        return AAAFactory.right().getByName(name);
    }

    public static IPrivilege getPrivilege(String name) {
        return AAAFactory.privilege().getByName(name);
    }

    /**
     * Create an authorizable by name (might be right or privilege)
     * @param name
     * @return
     */
    public static IAuthorizeable authorizeable(String name) {
        final IPrivilege privilege = getPrivilege(name);
        final IRight right = getRight(name);
        IAuthorizeable a = new IAuthorizeable.AuthorizeableBase() {
            @Override
            public IRight getRequiredRight() {
                return right;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return privilege;
            }
        };
        return a;
    }

    public static IAuthorizeable authByRight(String name) {
        final IRight right = getRight(name);
        IAuthorizeable a = new IAuthorizeable.AuthorizeableBase() {
            @Override
            public IRight getRequiredRight() {
                return right;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return null;
            }
        };
        return a;
    }

    public static IAuthorizeable authByPrivilege(String name) {
        final IPrivilege privilege = getPrivilege(name);
        IAuthorizeable a = new IAuthorizeable.AuthorizeableBase() {
            @Override
            public IRight getRequiredRight() {
                return null;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return privilege;
            }
        };
        return a;
    }

    public static IAuthorizeable authByRight(final IRight right) {
        IAuthorizeable a = new IAuthorizeable.AuthorizeableBase() {
            @Override
            public IRight getRequiredRight() {
                return right;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return null;
            }
        };
        return a;
    }

    public static IAuthorizeable authByPrivilege(final IPrivilege privilege) {
        IAuthorizeable a = new IAuthorizeable.AuthorizeableBase() {
            @Override
            public IRight getRequiredRight() {
                return null;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return privilege;
            }
        };
        return a;
    }

    public static IAuthorizeable authorizeable(final IPrivilege privilege, final IRight right) {
        IAuthorizeable a = new IAuthorizeable.AuthorizeableBase() {
            @Override
            public IRight getRequiredRight() {
                return right;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return privilege;
            }
        };
        return a;
    }

    // ----------------------------------------------

    /**
     * Create an authorizable by name (might be right or privilege)
     * @param name
     * @return
     */
    public static IAuthorizeable dynamicAuthorizeable(String name, final Object target) {
        final IPrivilege privilege = getPrivilege(name);
        final IRight right = getRight(name);
        IAuthorizeable a = new IAuthorizeable.AuthorizeableBase() {
            @Override
            public IRight getRequiredRight() {
                return right;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return privilege;
            }

            @Override
            public Object getTargetResource() {
                return target;
            }
        };
        return a;
    }

    public static IAuthorizeable dynamicAuthByRight(String name, final Object target) {
        final IRight right = getRight(name);
        IAuthorizeable a = new IAuthorizeable.AuthorizeableBase() {
            @Override
            public IRight getRequiredRight() {
                return right;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return null;
            }

            @Override
            public Object getTargetResource() {
                return target;
            }
        };
        return a;
    }

    public static IAuthorizeable dynamicAuthByRight(final IRight right, final Object target) {
        IAuthorizeable a = new IAuthorizeable.AuthorizeableBase() {
            @Override
            public IRight getRequiredRight() {
                return right;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return null;
            }

            @Override
            public Object getTargetResource() {
                return target;
            }
        };
        return a;
    }

    public static IAuthorizeable dynamicAuthorizeable(final IPrivilege privilege, final IRight right, final Object target) {
        IAuthorizeable a = new IAuthorizeable() {
            @Override
            public IRight getRequiredRight() {
                return right;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return privilege;
            }

            @Override
            public Object getTargetResource() {
                return target;
            }
        };
        return a;
    }

    public static boolean hasAccessTo(IAuthorizeable object) {
        IAccount me = me();
        if (null == me) return false;
        return me.hasAccessTo(object);
    }

    public static void checkAccess(IAuthorizeable object) throws NoAccessException {
        if (!hasAccessTo(object)) throw new NoAccessException();
    }

    public static boolean hasRight(String right, Object target) {
        return hasAccessTo(AAA.dynamicAuthByRight(right, target));
    }

    public static boolean hasRight(IRight right, Object target) {
        return hasAccessTo(AAA.dynamicAuthByRight(right, target));
    }

    public static boolean hasPrivilege(IPrivilege privilege) {
        return hasAccessTo(AAA.authByPrivilege(privilege));
    }

    public static boolean hasPrivilege(String privilege) {
        return hasAccessTo(AAA.authByPrivilege(privilege));
    }

    public static void checkRight(String right, Object target) throws NoAccessException {
        if (!hasRight(right, target)) throw new NoAccessException();
    }

    public static void checkRight(IRight right, Object target) throws NoAccessException {
        if (!hasRight(right, target)) throw new NoAccessException();
    }

    public static void checkPrivilege(String privilege) throws NoAccessException {
        if (!hasPrivilege(privilege)) throw new NoAccessException();
    }

    public static void checkPrivilege(IPrivilege privilege) throws NoAccessException {
        if (!hasPrivilege(privilege)) throw new NoAccessException();
    }

}
