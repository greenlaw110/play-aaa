package play.modules.aaa.utils;

import play.Play;
import play.modules.aaa.IAccount;
import play.modules.aaa.IPrivilege;
import play.modules.aaa.IRight;
import play.modules.aaa.IRole;

public class AAA {

    private static IPrivilege PRV_SUPERUSER;

    public static void _loadSuperUser() {
        String su_name = Play.configuration.getProperty("aaa.superuser", "superuser");
        PRV_SUPERUSER = AAAFactory.privilege().getByName(su_name);
    }

    public static IAccount currentUser() {
        return AAAFactory.account().getCurrent();
    }

    public static boolean isSuperUser() {
        return isSuperUser(currentUser());
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

    public static boolean hasRole(String... roles) {
        return hasRole(currentUser(), roles);
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
}
