package com.zwb.geekology.parser.lastfm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.zwb.config.api.ConfigurationFactory;
import com.zwb.config.api.IConfiguration;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.parser.IGkParser;
import com.zwb.geekology.parser.api.parser.IGkParserQuery;
import com.zwb.geekology.parser.api.parser.IGkParsingEvent.GkParsingEventType;
import com.zwb.geekology.parser.api.parser.IGkParsingResult;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.geekology.parser.impl.GkParsingResult;
import com.zwb.geekology.parser.impl.GkParsingSource;
import com.zwb.geekology.parser.lastfm.util.MyLogger;

import de.umass.lastfm.Artist;
import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Session;

public class GkParserLastFm implements IGkParser
{
	private MyLogger log = new MyLogger(this.getClass());
	private IConfiguration config = ConfigurationFactory.getConfiguration("lastfm.config");
	private IGkParsingSource source = new GkParsingSource("last.fm");
	private GkParsingResult result = new GkParsingResult(source);

	private String userAgent;
	private boolean debugMode;
	private String apiKey;
	private String secret;

	public GkParserLastFm()
	{
		log.debug("creating last.fm parser");
		userAgent = config.getString("access.user-agent", "tst");
		debugMode = config.getBool("access.debug-mode", false);
		apiKey = config.getString("access.apikey", "");
		secret = config.getString("access.secret", "");
		Session session;
		log.debug("configuring and authenticating last.fm access with userAgent=<"+userAgent+">, apiKey=<"+apiKey+">, secret=<"+secret+">, debugMode=<"+debugMode+">");
		
		Caller.getInstance().setUserAgent(userAgent);
	    Caller.getInstance().setDebugMode(debugMode);
	    session = Authenticator.getSession(Authenticator.getToken(apiKey), apiKey, secret);
	}
	
	@Override
	public IGkParsingResult parse(IGkParserQuery query)
	{
		if(query.isSampler())
		{
//			query for sampler
			log.debug("query for sampler "+query.getRelease());
		}
		else if (query.hasRelease())
		{
			log.debug("query for artist "+query.getArtist()+"with release "+query.getRelease());
			IGkDbArtist artist = this.queryArtist(query.getArtist(), query.getRelease());
		}
		else
		{
			log.debug("query for artist "+query.getArtist());
			IGkDbArtist artist = this.queryArtist(query.getArtist());
		}
		return null;
	}

	private IGkDbArtist queryArtist(String artistName)
	{
		Collection<Artist> artists = Artist.search(artistName, this.apiKey);
		List<String> matches = new ArrayList<>();
		Iterator<Artist> it = artists.iterator();
		while(it.hasNext())
		{
			matches.add(it.next().getName());
		}
		log.debug("query for artist <"+artistName+"> returned <"+artists.size()+"> matches: "+matches);
		this.result.addEvent(GkParsingEventType.ENTRY_FOUND, "query for artist <"+artistName+"> returned <"+artists.size()+"> matches");
		return null;
	}
	
	private IGkDbArtist queryArtist(String artistName, String releaseName)
	{
		//TODO
		return null;
	}
	
	private IGkDbRelease querySampler(String samplerName)
	{
//		TODO
		return null;
	}
	
	@Override
	public IGkParsingSource getSource() 
	{
		return this.source;
	}

}
