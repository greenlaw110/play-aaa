package controllers.aaa;

import play.modules.aaa.RequirePrivilege;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * A simple implementation of role/user/right/privilege admin tool
 */
public class Admin extends Controller {

    @Before
    @RequirePrivilege("superuser")
    public static void checkPermission() {
        //
    }

    public static void index() {
        render();
    }

    public static void addPrivilege(String name, int level) {
        render();
    }
}
