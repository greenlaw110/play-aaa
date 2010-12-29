package play.modules.aaa.morphia;

import play.modules.aaa.IPrivilege;
import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Id;

@SuppressWarnings("serial")
public abstract class GenericPrivilege extends Model implements IPrivilege {

   @Id
   private String name_;
   
   @com.google.code.morphia.annotations.Property("level")
   private int level_;

   // --- constructor ---
   GenericPrivilege() {}
   protected GenericPrivilege(String name, int level) {
      name_ = name;
      level_ = level;
   }
   
   // --- implement IPrivilege ---
   @Override
   public int compareTo(IPrivilege o) {
      int i0 = level_;
      int i1 = o.getLevel();
      return (i0 < i1 ? -1 : (i0 == i1 ? 0 : 1));
   }

   @Override
   public String getName() {
      return name_;
   }
   
   @Override
   public int getLevel() {
      return level_;
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
