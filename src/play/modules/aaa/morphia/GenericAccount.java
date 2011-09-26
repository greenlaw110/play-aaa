package play.modules.aaa.morphia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import play.libs.Crypto;
import play.modules.aaa.IAccount;
import play.modules.aaa.IAuthorizeable;
import play.modules.aaa.IPrivilege;
import play.modules.aaa.IRight;
import play.modules.aaa.IRole;
import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

@SuppressWarnings("serial")
public abstract class GenericAccount extends Model implements IAccount {

   @Id
   private String name_;
   
   @SuppressWarnings("unused")
   @com.google.code.morphia.annotations.Property("password")
   private String password_;
   
   @Reference("roles")
   private Set<IRole> roles_ = new HashSet<IRole>();
   
   @Reference("privilege")
   private IPrivilege privilege_;
   
   // --- constructor ---
   GenericAccount() {}
   protected GenericAccount(String name) {
      name_ = name;
   }
   
   // -- Standard common signatures
   public String toString() {
      return name_;
   }
   public boolean sameAccount(IAccount account) {
      return account.getName().equals(name_);
   }

   // --- accessors ---
   public IAccount setPassword(String password) {
      password_ = passwordHash(password);
      return this;
   }
   public IAccount assignRole(IRole... roles) {
      roles_.addAll(Arrays.asList(roles));
      return this;
   }
   public IAccount revokeRole(IRole... roles) {
      roles_.removeAll(Arrays.asList(roles));
      return this;
   }
   public IAccount assignPrivilege(IPrivilege privilege) {
      privilege_ = privilege;
      return this;
   }
   public IAccount revokePrivilege() {
      privilege_ = null;
      return this;
   }
   
   // --- utilities ---
   protected final String passwordHash(String password) {
      return Crypto.passwordHash(password + name_);
   }
   
   protected static final String passwordHash(String name, String password) {
      return Crypto.passwordHash(password + name);
   }
   
   // --- implement IAccount ---
   @Override
   public String getName() {
      return name_;
   }

   @Override
   public Collection<IRole> getRoles() {
      return new ArrayList<IRole>(roles_);
   }

   @Override
   public IPrivilege getPrivilege() {
      return privilege_;
   }

   @Override
   public boolean hasAccessTo(IAuthorizeable object) {
      IPrivilege reqP = object.getRequiredPrivilege();
      if (null != privilege_ && null != reqP) {
         if (privilege_.compareTo(reqP) > 0) return true;
      }
      IRight reqR = object.getRequiredRight();
      if (null == reqR) return false;
      
      for (IRole role: roles_) {
         for (IRight right: role.getRights()) {
            if (right.getName().equals(reqR.getName())) return true;
         }
      }
      return false;
   }
   
   // --- morphia model contract for user defined Id entities
   @Override
   public Object getId() {
      return name_;
   }
   @Override
   protected void setId_(Object id) {
      name_ = id.toString();
   }
   protected static Object processId_(Object id) {
      return id.toString();
   }   

}
