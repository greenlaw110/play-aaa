package play.modules.aaa;

public interface ISecurityHandler {

    boolean authentify(String username, String password);

    void onDisconnected();

    boolean check(String profile);
}
