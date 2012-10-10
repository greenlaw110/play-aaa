package play.modules.aaa.morphia;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import play.modules.aaa.IRight;
import play.modules.aaa.IRole;
import play.modules.morphia.Model;

import java.util.*;

@SuppressWarnings("serial")
public abstract class GenericRole extends AAAObject implements IRole {

    @Id
    private String name_;

    @Reference("rights")
    private Set<IRight> rights_ = new HashSet<IRight>();

    // --- constructor ---
    GenericRole() {
    }

    protected GenericRole(String name) {
        if (null == name) throw new NullPointerException();
        name_ = name;
    }

    // --- accessors ---
    @Override
    public IRole addRight(IRight right) {
        rights_.add(right);
        return this;
    }

    public IRole addRights(IRight... rights) {
        rights_.addAll(Arrays.asList(rights));
        return this;
    }

    public IRole addRights(Collection<IRight> rights) {
        rights_.addAll(rights);
        return this;
    }

    @Override
    public IRole removeRight(IRight right) {
        rights_.remove(right);
        return this;
    }

    public IRole removeRights(IRight... rights) {
        rights_.removeAll(Arrays.asList(rights));
        return this;
    }

    public IRole removeRights(Collection<IRight> rights) {
        rights_.removeAll(rights);
        return this;
    }

    // --- implement IRole ---
    @Override
    public String getName() {
        return name_;
    }

    @Override
    public Collection<IRight> getRights() {
        return new ArrayList<IRight>(rights_);
    }

    // --- morphia model contract for user defined Id entities
    @Override
    public Object getId() {
        return name_;
    }

    @Override
    protected void setId_(Object id) {
        name_ = id.toString();
    }

    protected static Object processId_(Object id) {
        return id.toString();
    }

}
