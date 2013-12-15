package play.modules.aaa;

import play.modules.aaa.utils.AAA;
import play.modules.aaa.utils.AAAFactory;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 22/10/13
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultSecurityHandler implements ISecurityHandler {
    @Override
    public boolean authentify(String username, String password) {
        IAccount account = AAAFactory.account().authenticate(username, password);
        return null != account;
    }

    @Override
    public void onDisconnected() {
        AAAFactory.account().setCurrent(null);
    }

    @Override
    public boolean check(String profile) {
        IAccount account = AAAFactory.account().getCurrent();
        if (null == account) return false;

        IAuthorizeable a = AAA.authorizeable(profile);
        return account.hasAccessTo(a);
    }
}
