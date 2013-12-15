package controllers;

import play.Logger;
import play.Play;
import play.modules.aaa.DefaultSecurityHandler;
import play.modules.aaa.ISecurityHandler;

import java.util.List;

public class Security extends Secure.Security {

    private static ISecurityHandler h;

    private static ISecurityHandler h() {
        if (null == h) {
            Class c = null;
            String sc = Play.configuration.getProperty("aaa.security_handler");
            if (null != sc && sc.trim().length() > 0) {
                try {
                    c = Class.forName(sc);
                } catch (ClassNotFoundException e) {
                    Logger.warn("Cannot find aaa security handler class: %s", sc);
                }
            }
            if (null == c) {
                List<Class> cl = Play.classloader.getAssignableClasses(ISecurityHandler.class);
                if (cl.size() > 0) {
                    c = cl.get(0);
                }
            }
            if (null != c) {
                try {
                    h = (ISecurityHandler)c.newInstance();
                } catch (Exception e) {
                    Logger.warn("cannot initialize security handler by class: %s", c.getName());
                }
            }
            if (null == h) {
                h = new DefaultSecurityHandler();
            }
        }
        return h;
    }

    static boolean authentify(String username, String password) {
        return h().authentify(username, password);
    }

    static boolean authenticate(String username, String password) {
        return h().authentify(username, password);
    }

    static void onDisconnected() {
        h().onDisconnected();
    }

    static boolean check(String profile) {
        return h().check(profile);
    }
}
