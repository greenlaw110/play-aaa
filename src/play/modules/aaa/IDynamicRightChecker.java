package play.modules.aaa;

/**
 * An implementation of dynamic right checker checks whether the "current"
 * {@link IAccount user} has access to "current" object. It is up to the
 * implementation to define how to get "current" user and "current" object. 
 * The implementation also defines the semantic of "access" 
 *  
 * @author greenlaw110@gmail.com
 */
public interface IDynamicRightChecker {
    boolean hasAccess();
}
