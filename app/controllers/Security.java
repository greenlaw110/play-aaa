package controllers;

import play.modules.aaa.IAccount;
import play.modules.aaa.utils.AAAFactory;

public class Security extends Secure.Security{
    
    static boolean authenticate(String username, String password) {
        IAccount account = AAAFactory.account().authenticate(username, password);
        return null != account;
    }
    
    static void onDisconnected() {
        AAAFactory.account().setCurrent(null);
    }
}
