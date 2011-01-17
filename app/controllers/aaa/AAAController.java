package controllers.aaa;

import play.Play;
import play.mvc.Controller;

public class AAAController extends Controller {
   protected static int getPageSize() {
     return Integer.parseInt(Play.configuration.getProperty("app.page.size", "20"));
   }
}
