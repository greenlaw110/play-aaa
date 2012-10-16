package play.modules.aaa;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import play.Play;
import play.modules.aaa.utils.AAA;
import play.modules.aaa.utils.AAAFactory;
import play.mvc.Controller;

public class PlayDynamicRightChecker extends Controller implements
        IDynamicRightChecker {

    public interface IAccessChecker<M> {
        boolean hasAccess(IAccount account, M model);
    }

    public static abstract class AccessCheckerBase<M> implements
            IAccessChecker<M> {
        protected AccessCheckerBase() {
            PlayDynamicRightChecker.registerChecker(this);
        }
        // @SuppressWarnings ("unchecked")
        // public Class<M> getTypeParameterClass() {
        // Type type = getClass().getGenericSuperclass();
        // ParameterizedType paramType = (ParameterizedType) type;
        // return (Class<M>) paramType.getActualTypeArguments()[0];
        // }
    }

    private static IAccessChecker<Object> defCheck_ = null;

    public static void setDefaultAccessChecker(IAccessChecker<Object> checker) {
        defCheck_ = checker;
    }

    private static Map<Class<?>, IAccessChecker<?>> checkers_ = new HashMap<Class<?>, IAccessChecker<?>>();

    public static void registerChecker(IAccessChecker<?> chkr) {
        Type[] types = chkr.getClass().getGenericInterfaces();
        Type type = null;
        for (Type t : types) {
            if (t.toString().indexOf("IAccessChecker") != -1) {
                type = t;
                break;
            }
        }
        ParameterizedType paramType = (ParameterizedType) type;
        @SuppressWarnings("rawtypes")
        Class c0 = (Class<?>) paramType.getActualTypeArguments()[0];

        checkers_.put(c0, chkr);
    }

    public static boolean hasAccessTo(Object obj) {
        if (null == obj) obj = AAA.targetResource();
        if (null == obj) return false;
        IAccount acc = (null == Play.configuration) ? null : AAA.currentAccount();
        if (null == acc) {
            return false;
        }

        Class<?> objClass = obj.getClass();
        @SuppressWarnings("rawtypes")
        IAccessChecker checker = checkers_.get(objClass);
        if (null == checker) {
            // try to match superclass, interface etc
            for (Class<?> c: checkers_.keySet()) {
                if (c.isAssignableFrom(objClass)) {
                    checker = checkers_.get(c);
                    if (null != checker) {
                        // cache the result so that next time
                        // it can be accessed instantly
                        checkers_.put(objClass, checker);
                        break;
                    }
                }
            }
        }
        if (null != checker) {
            return checker.hasAccess(acc, obj);
        }

        if (null == defCheck_)
            return false;
        return defCheck_.hasAccess(acc, obj);
    }

    @SuppressWarnings("unchecked")
    public static boolean _hasAccess() {
        return hasAccessTo(null);
    }

    @Override
    public boolean hasAccess() {
        return _hasAccess();
    }

    // @OnApplicationStart
    // public static class CheckerRegister extends Job<Object> {
    // @SuppressWarnings({ "rawtypes"})
    // @Override
    // public void doJob() {
    // List<Class> cl =
    // Play.classloader.getAssignableClasses(IAccessChecker.class);
    // for (Class c: cl) {
    // try {
    // registerChecker(c, (IAccessChecker)c.newInstance());
    // } catch (Exception e) {
    // throw new UnexpectedException(e);
    // }
    // }
    // }
    // }

    // @SuppressWarnings({ "rawtypes", "unchecked" })
    // private static void t_findCheckers_() {
    // Class[] ca = {DateChecker.class};
    // List<Class> cl = Arrays.asList(ca);
    //
    // for (Class c: cl) {
    // Type[] types = c.getGenericInterfaces();
    // Type type = null;
    // for (Type t: types) {
    // if (t.toString().indexOf("IAccessChecker") != -1) {
    // type = t;
    // break;
    // }
    // }
    // ParameterizedType paramType = (ParameterizedType) type;
    // Class c0 = (Class<?>)paramType.getActualTypeArguments()[0];
    // try {
    // registerChecker(c0, (IAccessChecker) c.newInstance());
    // } catch (InstantiationException e) {
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    //
    // public static class DateChecker implements IAccessChecker<Date> {
    // @Override
    // public boolean hasAccess(IAccount account, Date model) {
    // return (model.after(new Date()));
    // }
    // }
    //
    // public static void main(String[] args) {
    //
    // new AccessCheckerBase<String>(){
    // @Override
    // public boolean hasAccess(IAccount account, String model) {
    // return "good".equals(model);
    // }
    // };
    //
    // new AccessCheckerBase<Integer>(){
    // @Override
    // public boolean hasAccess(IAccount account, Integer model) {
    // return model == 110;
    // }
    // };
    //
    // t_findCheckers_();
    //
    // setCurrentObject("good");
    // System.out.println(_hasAccess());
    //
    // setCurrentObject(110);
    // System.out.println(_hasAccess());
    //
    // long l = System.currentTimeMillis();
    // setCurrentObject(new Date(l - 100000));
    // System.out.println(_hasAccess());
    //
    // setCurrentObject(new Date(l + 100000));
    // System.out.println(_hasAccess());
    // }

}
