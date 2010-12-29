package play.modules.aaa.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.db.jpa.JPQL;
import play.db.jpa.Model;
import play.libs.Crypto;
import play.modules.aaa.IAccount;
import play.modules.aaa.IAuthorizeable;
import play.modules.aaa.IPrivilege;
import play.modules.aaa.IRight;
import play.modules.aaa.IRole;
import play.mvc.Scope.Params;

@Entity
@Table(name="aaa_account")
public abstract class Account extends Model implements IAccount {
   private static final long serialVersionUID = -29941336917461289L;
   @Column(unique=true, updatable=false)
   public String name;
   public String password;
   @ManyToMany(cascade=CascadeType.MERGE)
   public Set<IRole> roles = new HashSet<IRole>();
   public IPrivilege privilege;
   
   // --- constructor ---
   public Account(String name) {
      if (null == name) throw new NullPointerException();
      this.name = name;
   }
   
   // -- Standard common signatures
   public String toString() {
      return name;
   }
   public boolean equals(Object obj) {
      if (obj == this) return true;
      if (!(obj instanceof Account)) return false;
      Account that = (Account)obj;
      return that.name == this.name;
   }
   public boolean sameAccount(IAccount account) {
      return account.getName().equals(name);
   }
   public int hashCode() {
      return name.hashCode();
   }
   
   // --- mutation methods ---
   public IAccount setPassword(String password) {
      this.password = getPasswordHash_(password);
      return this;
   }
   public IAccount assignRole(IRole... roles) {
      this.roles.addAll(Arrays.asList(roles));
      return this;
   }
   public IAccount revokeRole(IRole... roles) {
      this.roles.removeAll(Arrays.asList(roles));
      return this;
   }
   public IAccount assignPrivilege(IPrivilege privilege) {
      this.privilege = privilege;
      return this;
   }
   public IAccount revokePrivilege() {
      this.privilege = null;
      return this;
   }
   
   // --- utility methods ---
   private String getPasswordHash_(String password) {
      return Crypto.passwordHash(password + name);
   }
   
   // --- Implement IAccount ---
   @Override
   public String getName() {
      return name;
   }
   @Transient
   @Override
   public IAccount authenticate(String name, String password) {
      String p = getPasswordHash_(password);
      return find("byNameAndPassword", name, p).first();
   }
   @Override
   public Collection<IRole> getRoles() {
      return new ArrayList<IRole>(roles);
   }
   @Override
   public IPrivilege getPrivilege() {
      return privilege;
   }
   @Transient
   @Override
   public boolean hasAccessTo(IAuthorizeable object) {
      IPrivilege reqP = object.getRequiredPrivilege();
      if (null != privilege && null != reqP) {
         if (privilege.compareTo(reqP) > 0) return true;
      }
      IRight reqR = object.getRequiredRight();
      if (null == reqR) return false;
      
      for (IRole role: roles) {
         for (IRight right: role.getRights()) {
            if (right.equals(reqR)) return true;
         }
      }
      return false;
   }
   
   @Override
   public IAccount getCurrent() {
      return null;
   }
   
   // --- implements Model ---
   @SuppressWarnings("unchecked")
   public static Account create(String name, Params params) {
      try {
         return (Account) jpql_().create(entity_(), name, params);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
  }

  /**
   * Count entities
   * @return number of entities of this class
   */
  public static long count() {
     return jpql_().count(entity_());
  }

  /**
   * Count entities with a special query.
   * Example : Long moderatedPosts = Post.count("moderated", true);
   * @param query HQL query or shortcut
   * @param params Params to bind to the query
   * @return A long
   */
  public static long count(String query, Object... params) {
     return jpql_().count(entity_(), query, params);
  }

  /**
   * Find all entities of this type
   */
  @SuppressWarnings("unchecked")
public static List<Account> findAll() {
     return jpql_().findAll(entity_());
  }

  /**
   * Find the entity with the corresponding id.
   * @param id The entity id
   * @return The entity
   */
  @SuppressWarnings("unchecked")
public static Account findById(Object id) {
     try {
        return (Account) jpql_().findById(entity_(), id);
   } catch (Exception e) {
      throw new RuntimeException(e);
   }
  }

  /**
   * Prepare a query to find entities.
   * @param query HQL query or shortcut
   * @param params Params to bind to the query
   * @return A JPAQuery
   */
  public static JPAQuery find(String query, Object... params) {
     return jpql_().find(entity_(), query, params);
  }

  /**
   * Prepare a query to find *all* entities.
   * @return A JPAQuery
   */
  public static JPAQuery all() {
     return jpql_().all(entity_());
  }

  /**
   * Batch delete of entities
   * @param query HQL query or shortcut
   * @param params Params to bind to the query
   * @return Number of entities deleted
   */
  public static int delete(String query, Object... params) {
      return jpql_().delete(entity_(), query, params);
  }

  /**
   * Delete all entities
   * @return Number of entities deleted
   */
  public static int deleteAll() {
      return jpql_().deleteAll(entity_());
  }
  
  private static JPQL jpql_() {
     return JPQL.instance;
  }
  
  private static String entity_() {
     return Account.class.getName();
  }

}
