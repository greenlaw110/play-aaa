package controllers.aaa;

import java.util.ArrayList;
import java.util.List;

import play.modules.aaa.IAAAObject;
import play.modules.aaa.IAccount;
import play.modules.aaa.utils.Factory;

public class Accounts extends AAAController {

    public static void list(int page, String order, String orderBy,
            String search) throws Exception {
        if (page < 1)
            page = 1;
        IAccount fact = Factory.account();
        List<String> l = new ArrayList<String>();
        search = "null".equals(search) ? null : search;
        long count = fact._count(l, search, null);
        long totalCount = fact._count();
        int pageSize = getPageSize();
        long pageCount = ((count / pageSize) + (((count % pageSize) > 0) ? 1
                : 0));
        List<IAAAObject> accounts = fact._fetch((page - 1) * pageSize,
                pageSize, orderBy, order, l, search, null);

        if (request.isAjax()) {
            renderJSON2(accounts, page, count, totalCount, pageCount, pageSize);
        } else {
            render(accounts, page, count, totalCount, pageCount, pageSize);
        }
    }
}
