package play.modules.aaa.utils;

import play.jobs.Job;
import play.modules.aaa.IAccount;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/10/12
 * Time: 10:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class AAAJob<V> extends Job<V> {

    private final IAccount me = AAA.currentAccount();

    @Override
    public boolean init() {
        if (null != me) {
            play.modules.aaa.AAAContext.currentAccount(me);
        }
        return super.init();
    }
}
