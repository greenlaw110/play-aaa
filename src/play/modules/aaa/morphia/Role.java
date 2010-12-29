package play.modules.aaa.morphia;

import java.util.List;

import play.modules.morphia.Model;
import play.modules.morphia.MorphiaPlugin;
import play.mvc.Scope.Params;

import com.google.code.morphia.annotations.Entity;

@Entity("aaa_role")
public class Role extends GenericRole {

   private static final long serialVersionUID = 9162865672386231975L;

   Role() {}
   
   public Role(String name) {
      super(name);
   }
   
   // --- implement Morphia Model factory methods
   protected static play.db.Model.Factory mf = MorphiaPlugin.MorphiaModelLoader.getFactory(Role.class);
   public static play.db.Model.Factory getModelFactory() {
      return mf;
   }

   public static MorphiaQuery all() {
      return createQuery();
   }

   public static Model create(String name, Params params) {
      try {
         return Role.class.newInstance().edit(name, params.all());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public static MorphiaQuery createQuery() {
      return new play.modules.morphia.Model.MorphiaQuery(Role.class);
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
   public static List<Role> findAll() {
      return all().asList();
   }

   @SuppressWarnings("unchecked")
   public static Role findById(Object id) {
      return filter("_id", id.toString()).get();
   }

   public static MorphiaQuery filter(String property,
         Object value) {
      return find().filter(property, value);
   }

   @SuppressWarnings("unchecked")
   public static Role get() {
      return find().get();
   }

}
