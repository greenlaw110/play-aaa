package play.modules.aaa.morphia;

import org.bson.types.ObjectId;

import play.Logger;
import play.modules.aaa.IAccount;
import play.modules.aaa.ILog;
import play.modules.aaa.ILogService;
import play.modules.aaa.PlayLogService;
import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.PostLoad;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.annotations.Transient;

@SuppressWarnings("serial")
public abstract class GenericLog extends Model implements ILog {

   @com.google.code.morphia.annotations.Property("msg")
   private String msg_;
   
   @com.google.code.morphia.annotations.Property("lvl")
   @Indexed
   private String lvl_;
   
   @com.google.code.morphia.annotations.Property("ts")
   private long ts_;
   
   @com.google.code.morphia.annotations.Property("ack")
   @Indexed
   private boolean acknowledged_;
   
   @com.google.code.morphia.annotations.Property("p0")
   private String p0_;
   
   @Transient
   private IAccount principal_;
   
   @com.google.code.morphia.annotations.Property("p1")
   private String p1_;
   
   @Transient
   private IAccount acknowledger_;
   
   @SuppressWarnings("unused")
   @PrePersist
   private void storePrincipal_() {
      p0_ = principal_ == null ? null : principal_.getName();
      p1_ = acknowledger_ == null ? null : acknowledger_.getName();
   }
   
   @SuppressWarnings("unused")
   @PostLoad
   private void retreivePrincipal_() {
      try {
         principal_ = play.modules.aaa.utils.Factory.account().getByName(p0_);
      } catch (Exception e) {
         Logger.error(e, "error loading principal by name: %1$s", p0_);
      }
      try {
    	  acknowledger_ = play.modules.aaa.utils.Factory.account().getByName(p1_);
      } catch (Exception e) {
    	  Logger.error(e, "error loading acknowledger by name: %1$s", p1_);
      }
   }
   
   protected GenericLog() {}   
   protected GenericLog(IAccount principal, String level, String message) {
      principal_ = principal;
      lvl_ = level;
      msg_ = message;
      ts_ = System.currentTimeMillis();
   }
   
   @Override
   public String getMessage() {
      return msg_;
   }
   
   @Override
   public String getLevel() {
	   return lvl_;
   }

   @Override
   public long getTimestamp() {
      return ts_;
   }
   
   @Override
   public boolean acknowledged() {
	   return acknowledged_;
   }
   
   @Override
   public void acknowledge() {
	   try {
		   IAccount fact = play.modules.aaa.utils.Factory.account();
		   IAccount acc = fact.getCurrent();
		   if (null == acc) acc = fact.getSystemAccount();
		   acknowledger_ = acc;
		   acknowledged_ = true;
	   } catch (Exception e) {
		   throw new RuntimeException(e);
	   }
   }

   @Override
   public IAccount getPrincipal() {
      return principal_;
   }
   
   @Override
   public IAccount getAcknowledger() {
	   return acknowledger_;
   }
   
   private static ILogService sysLog_ = PlayLogService.get();
   protected ILogService sysLog() {
       return sysLog_;
   }
   public void registerSysLogHanlder(ILogService logService) {
       if (null != logService) {
           sysLog_ = logService;
       } else {
           Logger.warn("null sys log found");
       }
   }

   // --- morphia model contract for user defined Id entities
   @Id
   private ObjectId _id;
   @Override
   public Object getId() {
      return _id;
   }
   @Override
   protected void setId_(Object id) {
      _id = (id instanceof ObjectId) ? (ObjectId)id : new ObjectId(id.toString());
   }

}
