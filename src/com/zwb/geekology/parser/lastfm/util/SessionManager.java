package com.zwb.geekology.parser.lastfm.util;

import com.zwb.geekology.parser.lastfm.Config;

import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Session;

public class SessionManager
{
    private static SessionManager sessionMngr;
    private MyLogger log = new MyLogger(this.getClass());
    private Session session;	    
    
    public static SessionManager getInstance()
    {
	if(sessionMngr==null)
	{
	    sessionMngr = new SessionManager();
	}
	return sessionMngr;
    }
    
    private SessionManager()
    {
	    String userAgent = Config.getUserAgent();
	    boolean debugMode = Config.getDebugMode();
	    String apiKey = Config.getApiKey();
	    String secret = Config.getApiKeySecret();

	    this.log.debug("configuring and authenticating last.fm access with userAgent=<" + userAgent + ">, apiKey=<" + apiKey + ">, secret=<" + secret + ">, debugMode=<" + debugMode + ">");
	    Caller.getInstance().setUserAgent(userAgent);
	    Caller.getInstance().setDebugMode(debugMode);
	    session = Authenticator.getSession(Authenticator.getToken(apiKey), apiKey, secret);
    }
    
    public Session getSession()
    {
	return this.session;
    }
    
}
