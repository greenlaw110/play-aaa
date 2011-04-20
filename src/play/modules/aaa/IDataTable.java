package play.modules.aaa;

import java.util.List;

public interface IDataTable {
    public String _keyName();
    public Class<?> _keyType();
    public Object _keyValue(IAAAObject m);
    public IAAAObject _findById(Object id);
    public List<IAAAObject> _fetch(int offset, int length, String orderBy, String orderDirection, List<String> properties, String keywords, String where);
    public Long _count(List<String> properties, String keywords, String where);
    public long _count();
    public void _delete();
    public void _deleteAll();
}
