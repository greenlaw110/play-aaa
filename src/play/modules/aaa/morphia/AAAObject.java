package play.modules.aaa.morphia;

import com.google.code.morphia.annotations.PostLoad;
import play.modules.aaa.IAAAObject;
import play.modules.morphia.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 10/10/12
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AAAObject extends Model implements IAAAObject {
    @com.google.code.morphia.annotations.Property("prop")
    private Map<String, String> properties_;

    @PostLoad
    void initProperties() {
        if (null == properties_) properties_ = new HashMap<String, String>();
    }

    @Override
    public IAAAObject _setProperty(String key, String val) {
        properties_.put(key, val);
        return this;
    }

    @Override
    public String _getProperty(String key) {
        return properties_.get(key);
    }


}
