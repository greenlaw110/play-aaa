package play.modules.aaa;

/**
 * A <code>IRight</code> commutes between a {@link IRole} and a {@link IAthorizeable}. 
 * If a {@link IAuthorizeable} (action for example) require a certain <code>IRight</code>
 * then all {@link IRole roles} of the current {@ IAccount principal} will be enumerated
 * to see if any one of them has the same <code>IRight</code>
 * 
 * @author greenlaw110
 * @version 1.0 21/12/2010
 */
public interface IRight extends IDataTable, IAAAObject {
   /**
    * Return the name of this right. Two rights are considered equals if the name
    * of the two are equal
    * 
    * @return name of the right
    */
   String getName();
   
   /**
    * Whether this right is dynamic. An example of dynamic right is a customer has
    * right to access only orders owned by the customer, while order manager has 
    * right to access all orders which is a static right.
    * 
    * <p>In addition to normal {@link IAccount#hasAccessTo(IAuthorizeable) access check},
    * dynamic right also require the {@link IDynamicRightChecker#hasAccess() dynamic right 
    * check} to determine whether program should go or stop
    * 
    * @return true if this right is dynamic, false otherwise
    */
   boolean isDynamic();
   
   void setDynamic(boolean dynamic);
   
   /**
    * Factory method to find an instance with given name 
    * 
    * @param name
    * @return
    */
   IRight findByName(String name);
   
   /**
    * Factory method to create a right instance
    * @param name
    * @return
    */
   IRight create(String name);
}
