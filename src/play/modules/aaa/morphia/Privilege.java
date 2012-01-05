package play.modules.aaa.morphia;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import play.modules.aaa.IAAAObject;
import play.modules.aaa.IPrivilege;
import play.modules.morphia.MorphiaPlugin;
import play.mvc.Scope.Params;

import com.google.code.morphia.annotations.Entity;

@Entity("aaa_privilege")
public class Privilege extends GenericPrivilege {

    private static final long serialVersionUID = 9170911884781206973L;

    Privilege() {
    }

    public Privilege(String name, int level) {
        super(name, level);
        if (null == name)
            throw new NullPointerException();
    }

    @Override
    public IPrivilege findByName(String name) {
        return getByName(name);
    }
    
    @Override
    public IPrivilege getByName(String name) {
        return findById(name);
    }

    @Override
    public IPrivilege create(String name, int level) {
        return new Privilege(name, level);
    }

    // --- implement Morphia Model factory methods
    protected static play.db.Model.Factory mf = MorphiaPlugin.MorphiaModelLoader
            .getFactory(Privilege.class);

    public static play.db.Model.Factory getModelFactory() {
        return mf;
    }

    public static MorphiaQuery all() {
        return createQuery();
    }

    public static Privilege create(String name, Params params) {
        try {
            return Privilege.class.newInstance().edit(name, params.all());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MorphiaQuery createQuery() {
        return new play.modules.morphia.Model.MorphiaQuery(Privilege.class);
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

    public static MorphiaQuery find(String keys, Object... params) {
        return createQuery().findBy(keys.substring(2), params);
    }

    @SuppressWarnings("unchecked")
    public static List<Privilege> findAll() {
        return all().asList();
    }

    @SuppressWarnings("unchecked")
    public static Privilege findById(Object id) {
        return filter("_id", id.toString()).get();
    }

    public static MorphiaQuery filter(String property, Object value) {
        return find().filter(property, value);
    }

    @SuppressWarnings("unchecked")
    public static Privilege get() {
        return find().get();
    }

    @Override
    public String _keyName() {
        return "_id";
    }

    @Override
    public Class<?> _keyType() {
        return ObjectId.class;
    }

    @Override
    public Object _keyValue(IAAAObject m) {
        return getId();
    }

    @Override
    public IAAAObject _findById(Object id) {
        return findById(id);
    }

    @Override
    public List<IAAAObject> _fetch(int offset, int length, String orderBy,
            String orderDirection, List<String> properties, String keywords,
            String where) {
        List<IAAAObject> l = new ArrayList<IAAAObject>();
        for (play.db.Model m : getModelFactory().fetch(offset, length, orderBy,
                orderDirection, properties, keywords, where)) {
            l.add((Privilege) m);
        }
        return l;
    }

    @Override
    public Long _count(List<String> properties, String keywords, String where) {
        return getModelFactory().count(properties, keywords, where);
    }

    @Override
    public long _count() {
        return count();
    }

    @Override
    public void _deleteAll() {
        deleteAll();
    }

}
