package play.modules.aaa.utils;

import java.lang.reflect.Type;

import play.modules.aaa.ILog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LogJsonSerializer implements JsonSerializer<ILog> {
    @Override
    public JsonElement serialize(ILog src, Type srcType,
            JsonSerializationContext ctxt) {
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
    
    private static String toStr_(Object obj) {
        return null == obj ? null : obj.toString();
    }

}
