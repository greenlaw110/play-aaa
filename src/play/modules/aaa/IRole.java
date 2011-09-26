package play.modules.aaa;

import java.util.Collection;

/**
 * A <code>IRole</code> represent a group of @{link IRight} and can be granted to
 * an {@link IAccount}
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0 21/12/2010
 *
 */
public interface IRole extends IDataTable, IAAAObject {
   String getName();
   
   Collection<IRight> getRights();
   
   /**
    * Add a right to the role and return this role
    * @param right
    * @return
    */
   IRole addRight(IRight right);
   
   /**
    * Remove a right from this role and return this role
    * @param right
    * @return
    */
   IRole removeRight(IRight right);

   
   /**
    * Factory method to create a right instance
    * @param name
    * @return
    */
   IRole create(String name);
}
