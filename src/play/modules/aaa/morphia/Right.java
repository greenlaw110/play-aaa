package play.modules.aaa.morphia;

import java.util.List;

import play.modules.aaa.IRight;
import play.modules.morphia.MorphiaPlugin;
import play.mvc.Scope.Params;

import com.google.code.morphia.annotations.Entity;

@Entity("aaa_right")
public class Right extends GenericRight {

   private static final long serialVersionUID = 1023274531871083328L;

   Right() {}
   
   public Right(String name) {
      super(name);
   }
   
   public IRight create(String name) {
      return new Right(name);
   }
   
   // --- implement Morphia Model factory methods
   protected static play.db.Model.Factory mf = MorphiaPlugin.MorphiaModelLoader.getFactory(Right.class);
   public static play.db.Model.Factory getModelFactory() {
      return mf;
   }

   public static MorphiaQuery all() {
      return createQuery();
   }

   public static Right create(String name, Params params) {
      try {
         return Right.class.newInstance().edit(name, params.all());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public static MorphiaQuery createQuery() {
      return new play.modules.morphia.Model.MorphiaQuery(Right.class);
   }

   public static long count() {
      return Right.all().count();
   }

   public static long count(String keys, Object... params) {
      return Right.find(keys, params).count();
   }

   public static long deleteAll() {
      return Right.all().delete();
   }

   public static MorphiaQuery find() {
      return Right.createQuery();
   }

   public static MorphiaQuery find(String keys,
         Object... params) {
      return Right.createQuery().findBy(keys.substring(2), params);
   }

   @SuppressWarnings("unchecked")
   public static List<Right> findAll() {
      return all().asList();
   }

   @SuppressWarnings("unchecked")
   public static Right findById(Object id) {
      return filter("_id", id.toString()).get();
   }

   public static MorphiaQuery filter(String property,
         Object value) {
      return find().filter(property, value);
   }

   @SuppressWarnings("unchecked")
   public static Right get() {
      return find().get();
   }

}
