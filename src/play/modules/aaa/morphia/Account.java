package play.modules.aaa.morphia;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import play.Logger;
import play.Play;
import play.modules.aaa.IAAAObject;
import play.modules.aaa.IAccount;
import play.modules.aaa.IAuthenticator;
import play.modules.aaa.utils.ConfigConstants;
import play.modules.aaa.utils.LdapAuthenticator;
import play.modules.morphia.MorphiaPlugin;
import play.mvc.Scope.Params;

import com.google.code.morphia.annotations.Entity;

@Entity("aaa_account")
public class Account extends GenericAccount {

	private static final long serialVersionUID = -5243110444838677957L;

	public static IAccount sys_ = null;

	public Account(String name) {
		super(name);
		if (null == name)
			throw new NullPointerException();
	}

	/**
	 * for factory usage only
	 */
	Account() {
	}

	private static ThreadLocal<IAccount> cur_ = new ThreadLocal<IAccount>();

	@Override
	public IAccount authenticate(String name, String password) {
		return connect(name, password);
	}

	public static IAccount connect(String name, String password) {
		// is authenticator defined?
		String authImpl = Play.configuration
				.getProperty(ConfigConstants.AUTHENTICATOR_IMPL);
		IAuthenticator auth = null;
		if (null != authImpl) {
			if (authImpl.equalsIgnoreCase("ldap")) {
				auth = new LdapAuthenticator();
			} else {
				try {
					auth = (IAuthenticator) Class.forName(authImpl)
							.newInstance();
				} catch (Exception e) {
					Logger.fatal(e, "cannot load authenticator: %1$s", authImpl);
				}
			}
		}

		Account account = null;
		if (null != auth) {
			if (auth.authenticate(name, password)) {
				account = filter("_id", name).get();
				if (null == account) {
					account = new Account();
					account.setId(name);
					account.save();
				}
			}
		} else {
			account = filter("_id", name).filter("password",
					passwordHash(name, password)).get();
		}

		cur_.set(account);
		return account;
	}

	@Override
	public IAccount getCurrent() {
		return current();
	}

	public static IAccount current() {
		return cur_.get();
	}

	@Override
	public IAccount getSystemAccount() {
		return system();
	}

	public static IAccount system() {
		if (null == sys_) {
			String sys = Play.configuration.getProperty(
					ConfigConstants.SYS_ACCOUNT, SYSTEM);
			Account acc = Account.filter("_id", sys).get();
			if (null == acc) {
				acc = new Account(sys);
				acc.save();
			}
			sys_ = acc;
		}
		return sys_;
	}

	@Override
	public IAccount getByName(String name) {
		return byName(name);
	}

	public static IAccount byName(String name) {
		return null == name ? null : system().getName().equals(name) ? system()
				: Account.findById(name);
	}

	// --- implement Morphia Model factory methods
	protected static play.db.Model.Factory mf = MorphiaPlugin.MorphiaModelLoader
			.getFactory(Account.class);

	public static play.db.Model.Factory getModelFactory() {
		return mf;
	}

	public static MorphiaQuery all() {
		return createQuery();
	}

	public static Account create(String name, Params params) {
		try {
			return Account.class.newInstance().edit(name, params.all());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static MorphiaQuery createQuery() {
		return new play.modules.morphia.Model.MorphiaQuery(Account.class);
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
	public static List<Account> findAll() {
		return all().asList();
	}

	@SuppressWarnings("unchecked")
	public static Account findById(Object id) {
		return filter("_id", id.toString()).get();
	}

	public static MorphiaQuery filter(String property, Object value) {
		return find().filter(property, value);
	}

	@SuppressWarnings("unchecked")
	public static Account get() {
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
		return Account.findById(id);
	}

	@Override
	public List<IAAAObject> _fetch(int offset, int length, String orderBy,
			String orderDirection, List<String> properties, String keywords,
			String where) {
		List<IAAAObject> l = new ArrayList<IAAAObject>();
		for (play.db.Model m : Account.getModelFactory().fetch(offset, length,
				orderBy, orderDirection, properties, keywords, where)) {
			l.add((Account) m);
		}
		return l;
	}

	@Override
	public Long _count(List<String> properties, String keywords, String where) {
		return Account.getModelFactory().count(properties, keywords, where);
	}

	@Override
	public long _count() {
		return Account.count();
	}

	@Override
	public void _deleteAll() {
		Account.deleteAll();
	}

}
