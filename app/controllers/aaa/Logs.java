package controllers.aaa;

import java.util.ArrayList;
import java.util.List;

import play.modules.aaa.IAAAObject;
import play.modules.aaa.ILog;
import play.modules.aaa.utils.Factory;
import play.mvc.Router;

public class Logs extends AAAController {
    public static void index() {
        render();
    }

    public static void list(int page, String order, String orderBy,
            String search) throws Exception {
        if (page < 1)
            page = 1;
        ILog fact = Factory.log();
        List<String> l = new ArrayList<String>();
        search = "null".equals(search) ? null : search;
        long count = fact._count(l, search, null);
        long totalCount = fact._count();
        int pageSize = getPageSize();
        long pageCount = ((count / pageSize) + (((count % pageSize) > 0) ? 1
                : 0));
        List<IAAAObject> logs = fact._fetch((page - 1) * pageSize, pageSize,
                orderBy, order, l, search, null);
        
        String fetchUrl = Router.reverse("aaa.Logs.list").url;

        if (request.isAjax()) {
            renderJSON2(fact.getJsonSerializer(), logs, page, count, totalCount, pageCount, pageSize, search, fetchUrl);
        } else {
            index();
        }
    }
}
