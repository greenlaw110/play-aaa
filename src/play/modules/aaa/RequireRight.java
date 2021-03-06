package play.modules.aaa;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
public @interface RequireRight {
    String value();

    /**
     * Set the time of check method access permission, usually it should be true
     * But in rare case when it needs to check after the method executed (e.g. constructor)
     * then it could be set to false;
     * @return
     */
    boolean before() default true;

    /**
     * Indicate which object should be used to check against with account. This only
     * impact dynamic right checking
     *
     * <ul>
     *     <li>0: Set the current object on which the method is invoked to be the target.
     *         If the invoking method is static then it will set the first parameter as ;
     *         the target object. If there is no parameter at all then no target object
     *         will be set</li>
     *     <li>n (n > 0): Set the nth parameter as the target object. Note if n is larger
     *         than the number of parameters then a runtime exception will be thrown out
     *         when executing the method</li>
     *     <li>-1: Set the return object as the target object.
     *          <ul>Note
     *              <li>If there is no return object (method is void) then a runtime
     *                 exception will be thrown out</li>
     *              <li>When use -1 then the @{link #before()} shall be set to
     *                  false as permission check happened when the method returned.
     *                  if the -1 is used and before() is true, then enhancer will
     *                  throw out a RuntimeException</li>
     *              <li>When 0 is set to RR on a constructor, then -1 will be used instead</li>
     *          </ul>
     *     </li>
     *     <li>
     *         n (n < -1): Nothing will be set as target object
     *     </li>
     * </ul>
     */
    int target() default  0;
}
