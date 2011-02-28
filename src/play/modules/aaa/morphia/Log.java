package play.modules.aaa.morphia;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import play.Logger;
import play.modules.aaa.IAAAObject;
import play.modules.aaa.IAccount;
import play.modules.morphia.MorphiaPlugin;
import play.mvc.Scope.Params;

import com.google.code.morphia.annotations.Entity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Entity(value = "aaa_log", noClassnameStored = true)
public class Log extends GenericLog {

    private static final long serialVersionUID = 3458850233968068149L;

    Log() {
    }

    public JsonSerializer<?> getJsonSerializer() {
        return new JsonSerializer<Log>() {
            @Override
            public JsonElement serialize(Log src, Type srcType,
                    JsonSerializationContext ctx) {
                JsonObject j = new JsonObject();
                j.addProperty("id", src.getId().toString());
                j.addProperty("level", src.getLevel());
                j.addProperty("message", src.getMessage());
                j.addProperty("timestamp", src.getTimestamp());
                j.addProperty("principal", toStr_(src.getPrincipal()));
                j.addProperty("acknowledger", toStr_(src.getAcknowledger()));
                j.addProperty("acknowledged", src.acknowledged());
                return j;
            }

            private String toStr_(Object obj) {
                return null == obj ? null : obj.toString();
            }
        };
    }

    private Log(IAccount principal, String level, String message) {
        super(principal, level, message);
        if (null == message)
            throw new NullPointerException();
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
    public void log(String level, boolean autoAck, String message,
            Object... args) {
        IAccount acc = null;
        try {
            acc = play.modules.aaa.utils.Factory.account().getCurrent();
        } catch (Exception e) {
            Logger.error(e, "error get context account instance");
        }
        log(acc, autoAck, level, message, args);
    }

    @Override
    public void log(IAccount principal, boolean autoAck, String level,
            String message, Object... args) {
        sysLog().log(principal, autoAck, level, message, args);
        Log log = new Log(principal, level, String.format(message, args));
        if (autoAck)
            log.acknowledge();
        log.save();
    }

    @Override
    public Set<String> levels() {
    	//TODO get list from database
    	String[] levels = {"info", "error", "warn", "fatal"};
    	return new HashSet<String>(Arrays.asList(levels));
    };
    
    @Override
    public String levelFieldName() {
    	return "lvl";
    }
    
    @Override
    public String acknowledgeFieldName() {
    	return "ack";
    }

    // --- implement Morphia Model factory methods
    protected static play.db.Model.Factory mf = MorphiaPlugin.MorphiaModelLoader
            .getFactory(Log.class);

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

    public static MorphiaQuery find(String keys, Object... params) {
        return createQuery().findBy(keys.substring(2), params);
    }

    @SuppressWarnings("unchecked")
    public static List<Log> findAll() {
        return all().asList();
    }

    @SuppressWarnings("unchecked")
    public static Log findById(Object id) {
    	ObjectId oid = (id instanceof ObjectId) ? (ObjectId)id : new ObjectId(id.toString());
        return filter("_id", oid).get();
    }

    public static MorphiaQuery filter(String property, Object value) {
        return find().filter(property, value);
    }

    @SuppressWarnings("unchecked")
    public static Log get() {
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
        return Log.findById(id);
    }

    @Override
    public List<IAAAObject> _fetch(int offset, int length, String orderBy,
            String orderDirection, List<String> properties, String keywords,
            String where) {
        List<IAAAObject> l = new ArrayList<IAAAObject>();
        for (play.db.Model m : Log.getModelFactory().fetch(offset, length,
                orderBy, orderDirection, properties, keywords, where)) {
            l.add((Log) m);
        }
        return l;
    }

    @Override
    public Long _count(List<String> properties, String keywords, String where) {
        return Log.getModelFactory().count(properties, keywords, where);
    }

    @Override
    public long _count() {
        return Log.count();
    }

    @Override
    public void _deleteAll() {
        Log.deleteAll();
    }
}
