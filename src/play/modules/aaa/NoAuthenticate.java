package play.modules.aaa;

import java.lang.annotation.*;

/**
 * Indicate that the annotated method is not subject to Secure authentication
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface NoAuthenticate {
}
