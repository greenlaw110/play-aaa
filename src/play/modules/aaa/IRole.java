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

   IRole addRights(IRight... rights);

   IRole addRights(Collection<IRight> rights);

   /**
    * Remove a right from this role and return this role
    * @param right
    * @return
    */
   IRole removeRight(IRight right);

   IRole removeRights(IRight... rights);

   IRole removeRights(Collection<IRight> rights);


    /**
     * Factory method to return a role instance from given name.
     * @param name
     * @return role instance or <code>null</code> if not found by the name
     */
    IRole getByName(String name);

    /**
    * Factory method to create a right instance
    * @param name
    * @return
    */
   IRole create(String name);
}
