package play.modules.aaa;

public class AAAContext {
    public IAccount currentUser;

    public AAAContext(IAccount currentUser) {
        this.currentUser = currentUser;
    }

    private static final ThreadLocal<AAAContext> _current = new ThreadLocal<AAAContext>();

    public static AAAContext get() {
        return _current.get();
    }

    public static void clear() {
        _current.remove();
    }

    public static void currentAccount(IAccount account) {
        _current.set(new AAAContext(account));
    }

    public static IAccount currentAccount() {
        AAAContext ctx = _current.get();
        return null == ctx ? null : ctx.currentUser;
    }
}
