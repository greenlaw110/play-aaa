package controllers;

import play.modules.aaa.IAccount;
import play.modules.aaa.IAuthorizeable;
import play.modules.aaa.IPrivilege;
import play.modules.aaa.IRight;
import play.modules.aaa.utils.AAAFactory;
import controllers.Secure;

public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
        IAccount account = AAAFactory.account().authenticate(username, password);
        return null != account;
    }

    static void onDisconnected() {
        AAAFactory.account().setCurrent(null);
    }

    static boolean check(String profile) {
        IAccount account = AAAFactory.account().getCurrent();
        if (null == account) return false;

        final IPrivilege privilege = AAAFactory.privilege().getByName(profile);
        final IRight right = AAAFactory.right().getByName(profile);
        IAuthorizeable a = new IAuthorizeable() {
            @Override
            public IRight getRequiredRight() {
                return right;
            }

            @Override
            public IPrivilege getRequiredPrivilege() {
                return privilege;
            }
        };
        return account.hasAccessTo(a);
    }
}
