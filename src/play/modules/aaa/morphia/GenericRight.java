package play.modules.aaa.morphia;

import play.modules.aaa.IRight;
import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Id;

@SuppressWarnings("serial")
public abstract class GenericRight extends Model implements IRight {

   @Id
   private String name_;
   
   @com.google.code.morphia.annotations.Property("dy")
   private boolean isDynamic_;
   
   // --- constructor ---
   GenericRight() {}
   protected GenericRight(String name) {
      if (null == name) throw new NullPointerException();
      name_ = name;
   }
   
   // --- implement IRight ---
   @Override
   public String getName() {
      return name_;
   }
   
   @Override
   public boolean isDynamic(){
       return isDynamic_;
   }
   
   @Override
   public void setDynamic(boolean dynamic) {
       isDynamic_ = dynamic;
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
