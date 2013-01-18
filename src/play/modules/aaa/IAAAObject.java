package play.modules.aaa;

public interface IAAAObject {
    void _save();

    /**
     * Set property to the AAA object specified by key
     * 
     * if val is null then remove this property specified by the key
     * 
     * @param key
     * @param val
     * @return
     */
    IAAAObject _setProperty(String key, String val);
    String _getProperty(String key);
}
