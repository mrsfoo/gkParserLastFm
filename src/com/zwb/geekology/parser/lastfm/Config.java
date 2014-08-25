package com.zwb.geekology.parser.lastfm;

import com.zwb.config.api.ConfigurationFactory;
import com.zwb.config.api.IConfiguration;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.geekology.parser.impl.GkParsingResult;
import com.zwb.geekology.parser.impl.GkParsingSource;

public class Config 
{
	private static final String SOURCE_STRING = "last.fm";
	
	private static final String CONFIG_NAME = "lastfm.config";
	private static final String CONFIG_KEY_USER_AGENT = "access.user-agent";
	private static final String CONFIG_KEY_DEBUG_MODE = "access.debug-mode";
	private static final String CONFIG_KEY_API_KEY = "access.apikey";
	private static final String CONFIG_KEY_API_KEY_SECRET = "access.secret";
	
	private static IConfiguration config = ConfigurationFactory.getBufferedConfiguration(CONFIG_NAME);

	public static String getSourceString()
	{
		return SOURCE_STRING;
	}
	
	public static String getUserAgent()
	{
		return config.getString(CONFIG_KEY_USER_AGENT, "tst");
	}
	
	public static boolean getDebugMode()
	{
		return config.getBool(CONFIG_KEY_DEBUG_MODE, false);
	}
	
	public static String getApiKey()
	{
		return config.getString(CONFIG_KEY_API_KEY, "");
	}
	
	public static String getApiKeySecret()
	{
		return config.getString(CONFIG_KEY_API_KEY_SECRET, "");
	}
	
}
