package play.modules.aaa;

import java.util.Collection;

/**
 * An <code>IAccount</code> is an abstraction of a principal request authorization
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0 21/12/2010
 */
public interface IAccount extends IDataTable, IAAAObject {
   /**
    * Return name of this account
    * @return
    */
   String getName();
   
   /**
    * Factory method to return an {@link IAccount} with given
    * name and password.
    * 
    * <p>Implementation of this method will in the end go to 
    * account storage to search for a matched records with given
    * name and password. The implementation might perform hash
    * on password before searching for the sake of security
    * 
    * @param name the given account name
    * @param password the given account password
    * @return an account instance if name and password match one record found
    * in account storage or <code>null</code> if no matching found
    */
   IAccount authenticate(String name, String password);
      
   /**
    * Return a collection of roles assigned to this account
    * @return all roles assigned to this account or an empty collection if no roles has been assigned
    * to this account
    */
   Collection<IRole> getRoles();
   
   /**
    * Return this account's privilege
    * 
    * @return this account's privilege or null if this account has not been assigned with a privilege
    */
   IPrivilege getPrivilege();
   
   /**
    * Determine whether this account has access to the given {@link IAuthorizeable} object.
    * 
    * <p>The implementation shall check both {@link IPrivilege} and {@link IRole roles} and hence
    * {@link IRight rights} of this account against the requried {@link IAuthorizeable#getRequiredPrivilege() 
    * privilege} and {@link IAuthorizeable#getRequiredRight()} of the given object
    * 
    * @param object which needs certain authority to access
    * @return true if this account has access to the given object, false otherwise
    */
   boolean hasAccessTo(IAuthorizeable object);

   /**
    * Set password to this account and return this account
    * 
    * @param password
    * @return
    */
   IAccount setPassword(String password);
   
   /**
    * Assign role to this account and return this account
    * @param roles
    * @return
    */
   IAccount assignRole(IRole... roles);
   
   /**
    * Revoke role from this account and return this account
    * @param roles
    * @return
    */
   IAccount revokeRole(IRole... roles);
   
   /**
    * Assign privilege to this account and return this account
    * @param privilege
    * @return
    */
   IAccount assignPrivilege(IPrivilege privilege);
   
   /**
    * Remvoke privilege from this account and return this account
    * @return
    */
   IAccount revokePrivilege();
   
   /**
    * Factory method to return an account instance from given name.
    * @param name
    * @return account instance or <code>null</code> if not found by the name
    */
   IAccount getByName(String name);
   
   /**
    * Factory method to return an account instance associated with current execution context. A
    * typical implementation might be get from a ThreadLocal package or system cache with session key etc. 
    * 
    * @return
    */
   IAccount getCurrent();
   
   /**
    * Factory method to return a system account instance
    * @return
    */
   IAccount getSystemAccount();
   
   /**
    * The recommended system account name. However it's subject to account implementation to determine
    * whether use it or not
    */
   public final String SYSTEM = "_system";
}
