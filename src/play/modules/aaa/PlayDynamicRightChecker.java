package play.modules.aaa;

import play.modules.aaa.utils.Factory;
import play.mvc.Controller;

public class PlayDynamicRightChecker extends Controller implements
        IDynamicRightChecker {

    private static ThreadLocal<play.db.Model> curObj_ = new ThreadLocal<play.db.Model>();

    public static void setCurrentObject(play.db.Model model) {
        curObj_.set(model);
    }
    
    public interface IAccessChecker {
        boolean hasAccess(IAccount account, play.db.Model model);
    }
    private static IAccessChecker checker_ = null;
    public static void setAccessChecker(IAccessChecker checker) {
        checker_ = checker;
    }
    
    public static boolean _hasAccess() {
        if (null == checker_) return false;
        
        play.db.Model model = curObj_.get();
        IAccount acc = Factory.account().getCurrent();
        if (model == null || Factory.account().getCurrent() == null) {
            return false;
        }
        return checker_.hasAccess(acc, model);
    }
    
    @Override
    public boolean hasAccess() {
        return _hasAccess();
    }

}
