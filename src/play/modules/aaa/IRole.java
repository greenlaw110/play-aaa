package play.modules.aaa;

import java.util.Collection;

import play.modules.aaa.morphia.Right;

/**
 * A <code>IRole</code> represent a group of @{link IRight} and can be granted to
 * an {@link IAccount}
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0 21/12/2010
 *
 */
public interface IRole {
   String getName();
   
   Collection<IRight> getRights();
   
   /**
    * Add a right to the role and return this role
    * @param right
    * @return
    */
   IRole addRight(Right right);
   
   /**
    * Remove a right from this role and return this role
    * @param right
    * @return
    */
   IRole removeRight(Right right);
}
