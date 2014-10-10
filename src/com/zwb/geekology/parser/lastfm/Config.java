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
    private static final String CONFIG_DEFAULT_USER_AGENT = "tst";
    private static final String CONFIG_KEY_DEBUG_MODE = "access.debug-mode";
    private static final boolean CONFIG_DEFAULT_DEBUG_MODE = false;
    private static final String CONFIG_KEY_API_KEY = "access.apikey";
    private static final String CONFIG_KEY_API_KEY_SECRET = "access.secret";
    private static final String CONFIG_KEY_SEARCH_DEPTH = "search.depth";
    private static final int CONFIG_DEFAULT_SEARCH_DEPTH = 15;
    private static final String CONFIG_KEY_SEARCH_THRESHOLD = "search.threshold";
    private static final double CONFIG_DEFAULT_SEARCH_THRESHOLD = 0.7;
    private static final String CONFIG_KEY_SEARCH_THRESHOLD_ALBUM = "search.threshold.album";
    private static final double CONFIG_DEFAULT_SEARCH_THRESHOLD_ALBUM = 0.98;
    
    private static IConfiguration config = ConfigurationFactory.getBufferedConfiguration(CONFIG_NAME);
    
    public static String getSourceString()
    {
	return SOURCE_STRING;
    }
    
    public static String getUserAgent()
    {
	return config.getString(CONFIG_KEY_USER_AGENT, CONFIG_DEFAULT_USER_AGENT);
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
    
    public static int getSearchDepth()
    {
	return config.getInt(CONFIG_KEY_SEARCH_DEPTH, CONFIG_DEFAULT_SEARCH_DEPTH);
    }
    
    public static double getSearchTreshold()
    {
	return config.getDouble(CONFIG_KEY_SEARCH_THRESHOLD, CONFIG_DEFAULT_SEARCH_THRESHOLD);
    }
    
    public static double getSearchTresholdViaAlbum()
    {
	return config.getDouble(CONFIG_KEY_SEARCH_THRESHOLD_ALBUM, CONFIG_DEFAULT_SEARCH_THRESHOLD_ALBUM);
    }
    
}
