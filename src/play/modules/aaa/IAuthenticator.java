package play.modules.aaa;

/**
 * Defines an authenticate interface
 * @author luog
 */
public interface IAuthenticator {
	boolean authenticate(String name, String password);
}
