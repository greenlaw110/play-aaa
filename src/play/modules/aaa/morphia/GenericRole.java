package play.modules.aaa.morphia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import play.modules.aaa.IRight;
import play.modules.aaa.IRole;
import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

@SuppressWarnings("serial")
public class GenericRole extends Model implements IRole {

   @Id
   private String name_;
   
   @Reference
   private Set<IRight> rights_ = new HashSet<IRight>();
   
   // --- constructor ---
   GenericRole() {}
   protected GenericRole(String name) {
      if (null == name) throw new NullPointerException();
      name_ = name;
   }
   
   // --- accessors ---
   @Override
   public IRole addRight(Right right) {
      rights_.add(right);
      return this;
   }
   
   @Override
   public IRole removeRight(Right right) {
      rights_.remove(right);
      return this;
   }
   
   // --- implement IRole ---
   @Override
   public String getName() {
      return name_;
   }
   
   @Override
   public Collection<IRight> getRights() {
      return new ArrayList<IRight>(rights_);
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
