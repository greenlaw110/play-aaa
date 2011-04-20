package controllers.aaa;

import java.util.ArrayList;
import java.util.List;

import models.aaa.Filter;
import play.modules.aaa.IAAAObject;
import play.modules.aaa.IRight;
import play.modules.aaa.utils.Factory;
import play.mvc.Router;
import play.mvc.results.RenderJson;

public class Rights extends AAAController {
	
    private static void list_() {
    	redirect(Router.reverse("aaa.Rights.list").url);
    }
    
    public static void list(int page, String sort, String sortBy,
            String search, List<Filter> filters) throws Exception {
    	if (!request.isAjax()) {
    		render();
    	}
        if (page < 1)
            page = 1;
        IRight fact = Factory.right();
        List<String> l = new ArrayList<String>();
        search = "null".equals(search) ? null : search;
    	if (sortBy == null) {
    		sortBy = "name_"; //TODO remove hardcode field name
    		sort = "DESC";
    	}
        long count = fact._count(l, search, Filter.toString(filters));
        long totalCount = fact._count();
        int pageSize = getPageSize();
        long pageCount = ((count / pageSize) + (((count % pageSize) > 0) ? 1
                : 0));
        if (page > pageCount) page = 1;
        List<IAAAObject> rights = fact._fetch((page - 1) * pageSize, pageSize,
                sortBy, sort, l, search, Filter.toString(filters));

        String fetchUrl = Router.reverse("aaa.Rights.list").url;

        renderJSON2(rights, page, count, totalCount, pageCount, pageSize, search, sort, sortBy, fetchUrl, filters);
    }
    
    public static void saveNew(String name) {
    	IRight fact = Factory.right();
    	IRight right = fact.create(name);
    	right._save();
    	renderJSON2(right);
    }
    
    public static void saveUpdate(String name) {
    	
    }
    
}
