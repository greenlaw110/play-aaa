package controllers.aaa;

import java.util.ArrayList;
import java.util.List;

import models.aaa.Filter;

import play.modules.aaa.IAAAObject;
import play.modules.aaa.ILog;
import play.modules.aaa.utils.Factory;
import play.mvc.Router;

public class Logs extends AAAController {
    public static void index() {
        render();
    }
    
    private static Filter levelFilter_(ILog logFact) {
    	Filter f = new Filter();
    	f.property = logFact.levelFieldName();
    	for (String s: logFact.levels()) {
    		f.addOption(s);
    	}
    	return f;
    }
    
    private static Filter acknowledgeFilter_(ILog logFact) {
    	Filter f = new Filter();
    	f.property = logFact.acknowledgeFieldName();
    	f.addOption("true");
    	f.addOption("false", true);
    	return f;
    }

    public static void list(int page, String order, String orderBy,
            String search, List<Filter> filters) throws Exception {
        if (page < 1)
            page = 1;
        ILog fact = Factory.log();
        List<String> l = new ArrayList<String>();
        search = "null".equals(search) ? null : search;
        if (null == filters) {
            filters = new ArrayList<Filter>();
            filters.add(levelFilter_(fact));
            filters.add(acknowledgeFilter_(fact));
        }
        long count = fact._count(l, search, Filter.toString(filters));
        long totalCount = fact._count();
        int pageSize = getPageSize();
        long pageCount = ((count / pageSize) + (((count % pageSize) > 0) ? 1
                : 0));
        List<IAAAObject> logs = fact._fetch((page - 1) * pageSize, pageSize,
                orderBy, order, l, search, Filter.toString(filters));

        String fetchUrl = Router.reverse("aaa.Logs.list").url;

        if (request.isAjax()) {
            renderJSON2(fact.getJsonSerializer(), logs, page, count,
                    totalCount, pageCount, pageSize, search, fetchUrl, filters);
        } else {
            index();
        }
    }
    
    public static void acknowledge(String id) throws Exception {
    	ILog fact = Factory.log();
    	ILog log = (ILog)fact._findById(id);
    	notFoundIfNull(log);
    	if (!log.acknowledged()) {
    		log.acknowledge();
    		log._save();
    	}
    	renderText("ok");
    }
}
