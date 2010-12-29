package play.modules.aaa.jpa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;

import play.db.jpa.Model;
import play.modules.aaa.IAccount;
import play.modules.aaa.IAuthorizeable;
import play.modules.aaa.IPrivilege;
import play.modules.aaa.IRole;

@MappedSuperclass
public abstract class GenericAccount extends Model implements IAccount {
   @Column(name="name", nullable=false,unique=true,updatable=false)
   private String name_;
   
   @Column(name="password")
   private String password_;

   @ManyToMany(cascade=CascadeType.MERGE)
   private Set<IRole> roles_ = new HashSet<IRole>();

   public IPrivilege privilege;

   // --- implement IAccount ---
   @Override
   public String getName() {
      return name_;
   }

   @Override
   public IAccount authenticate(String name, String password) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Collection<IRole> getRoles() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public IPrivilege getPrivilege() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean hasAccessTo(IAuthorizeable object) {
      // TODO Auto-generated method stub
      return false;
   }

}
