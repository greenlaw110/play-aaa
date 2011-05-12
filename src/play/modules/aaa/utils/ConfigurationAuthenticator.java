package play.modules.aaa.utils;

import java.util.HashMap;
import java.util.Map;

import org.h2.util.StringUtils;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.aaa.IAuthenticator;

/**
 * Implement simple and basic authenticate mechanism using username and password defined in play application.conf file.
 * 
 * aaa.auth.credentials=user1:pass1,user2:pass2...
 * 
 * @author luog
 */
public class ConfigurationAuthenticator implements IAuthenticator {
    
    public static final String CONF_CREDENTIALS = "aaa.auth.credentials";

    @Override
    public boolean authenticate(String username, String password) {
        String pass = credentials_.get(username);
        
        return StringUtils.equals(pass, password);
    }
    
    private static final Map<String,String> credentials_ = new HashMap<String,String>(); 
    
    @OnApplicationStart
    public static class BootLoader extends Job<Object> {
        @Override
        public void doJob() {
            String creds = Play.configuration.getProperty(CONF_CREDENTIALS);
            if (null != creds) {
                String[] sa = creds.split(",");
                for (String cred: sa) {
                    String[] sa2 = cred.split(":");
                    if (sa2.length > 1) {
                        credentials_.put(sa2[0], sa2[1]);
                    }
                }
            }
        }
    }

}
