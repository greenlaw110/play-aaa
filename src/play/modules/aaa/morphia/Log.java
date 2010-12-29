package play.modules.aaa.morphia;

import java.util.List;

import play.Logger;
import play.modules.aaa.IAccount;
import play.modules.morphia.MorphiaPlugin;
import play.mvc.Scope.Params;

import com.google.code.morphia.annotations.Entity;

@Entity(value="aaa_log", noClassnameStored=true)
public class Log extends GenericLog {
   
   private static final long serialVersionUID = 3458850233968068149L;

   Log() {}
   
   private Log(IAccount principal, String level, String message) {
      super(principal, level, message);
      if (null == message) throw new NullPointerException();
   }

   @Override
   public void log(String level, String message, Object... args) {
      IAccount acc = null;
      try {
         acc = play.modules.aaa.utils.Factory.account().getCurrent();
      } catch (Exception e) {
         Logger.error(e, "error get context account instance");
      }
      log(acc, false, level, message, args);
   }

   @Override
   public void log(String level, boolean autoAck, String message, Object... args) {
      IAccount acc = null;
      try {
         acc = play.modules.aaa.utils.Factory.account().getCurrent();
      } catch (Exception e) {
         Logger.error(e, "error get context account instance");
      }
      log(acc, autoAck, level, message, args);
   }

   @Override
   public void log(IAccount principal, boolean autoAck, String level, String message, Object... args) {
      Log log = new Log(principal, level, String.format(message, args));
      if (autoAck) log.acknowledge();
      log.save();
   }

   // --- implement Morphia Model factory methods
   protected static play.db.Model.Factory mf = MorphiaPlugin.MorphiaModelLoader.getFactory(Log.class);
   public static play.db.Model.Factory getModelFactory() {
      return mf;
   }

   public static MorphiaQuery all() {
      return createQuery();
   }

   public static Log create(String name, Params params) {
      try {
         return Log.class.newInstance().edit(name, params.all());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public static MorphiaQuery createQuery() {
      return new play.modules.morphia.Model.MorphiaQuery(Log.class);
   }

   public static long count() {
      return all().count();
   }

   public static long count(String keys, Object... params) {
      return find(keys, params).count();
   }

   public static long deleteAll() {
      return all().delete();
   }

   public static MorphiaQuery find() {
      return createQuery();
   }

   public static MorphiaQuery find(String keys,
         Object... params) {
      return createQuery().findBy(keys.substring(2), params);
   }

   @SuppressWarnings("unchecked")
   public static List<Log> findAll() {
      return all().asList();
   }

   @SuppressWarnings("unchecked")
   public static Log findById(Object id) {
      return filter("_id", id.toString()).get();
   }

   public static MorphiaQuery filter(String property,
         Object value) {
      return find().filter(property, value);
   }

   @SuppressWarnings("unchecked")
   public static Log get() {
      return find().get();
   }
}
