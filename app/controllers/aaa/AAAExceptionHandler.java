package controllers.aaa;

import controllers.Secure;
import play.Logger;
import play.modules.aaa.NoAccessException;
import play.mvc.Catch;
import play.mvc.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 6/01/12
 * Time: 10:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class AAAExceptionHandler extends Controller {
    @Catch(NoAccessException.class)
    public static void handleNoAccess(NoAccessException e) throws Throwable {
        if (session.contains("username")) {
            Logger.warn(e, "no access for user %s", session.get("username"));
            forbidden(e.getMessage());
        }
        else Secure.login();
    }
}
