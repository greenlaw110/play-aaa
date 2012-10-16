package controllers;

import play.modules.aaa.IAccount;
import play.modules.aaa.IAuthorizeable;
import play.modules.aaa.IPrivilege;
import play.modules.aaa.IRight;
import play.modules.aaa.utils.AAA;
import play.modules.aaa.utils.AAAFactory;
import controllers.Secure;

public class Security extends Secure.Security {

    static boolean authentify(String username, String password) {
        IAccount account = AAAFactory.account().authenticate(username, password);
        return null != account;
    }

    static boolean authenticate(String username, String password) {
        return authentify(username, password);
    }

    static void onDisconnected() {
        AAAFactory.account().setCurrent(null);
    }

    static boolean check(String profile) {
        IAccount account = AAAFactory.account().getCurrent();
        if (null == account) return false;

        IAuthorizeable a = AAA.authorizeable(profile);
        return account.hasAccessTo(a);
    }
}
