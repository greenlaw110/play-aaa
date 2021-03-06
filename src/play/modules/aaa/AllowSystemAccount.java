package play.modules.aaa;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate {@link IAccount#getSystemAccount() system} account could be used if {@link IAccount#getCurrent() current}
 * account is <code>null</code>
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0 23/12/2010
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
public @interface AllowSystemAccount {
}
