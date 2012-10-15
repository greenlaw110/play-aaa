package play.modules.aaa;

import play.jobs.Job;
import play.modules.aaa.utils.AAA;
import play.modules.aaa.utils.AAAFactory;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/10/12
 * Time: 10:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class AAAJob<V> extends Job<V> {

    public static class AAAContext {
        public IAccount currentUser;
        public AAAContext(IAccount currentUser) {
            this.currentUser = currentUser;
        }
        private static final ThreadLocal<AAAContext> _current = new ThreadLocal<AAAContext>();
        public static AAAContext get() {
            return _current.get();
        }

        public static IAccount currentUser() {
            AAAContext ctx = _current.get();
            return null == ctx ? null : ctx.currentUser;
        }
    }

    private final IAccount me = AAA.currentUser();

    @Override
    public boolean init() {
        AAAContext._current.set(new AAAContext(me));
        return super.init();
    }
}
