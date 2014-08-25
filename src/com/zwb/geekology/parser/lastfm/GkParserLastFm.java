package com.zwb.geekology.parser.lastfm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jdk.nashorn.internal.parser.AbstractParser;

import com.zwb.geekology.parser.abstr.db.AbstrGkParser;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.exception.GkParserException;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.api.parser.IGkParser;
import com.zwb.geekology.parser.api.parser.IGkParserQuery;
import com.zwb.geekology.parser.api.parser.IGkParsingResult;
import com.zwb.geekology.parser.api.parser.IGkParsingResultArtist;
import com.zwb.geekology.parser.api.parser.IGkParsingResultSampler;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.geekology.parser.enums.GkParsingEventType;
import com.zwb.geekology.parser.enums.GkParsingState;
import com.zwb.geekology.parser.impl.GkParsingResult;
import com.zwb.geekology.parser.impl.GkParsingResultArtist;
import com.zwb.geekology.parser.impl.GkParsingResultSampler;
import com.zwb.geekology.parser.impl.GkParsingSource;
import com.zwb.geekology.parser.impl.utils.GkParserCommonUtils;
import com.zwb.geekology.parser.lastfm.db.GkDbArtistLastFm;
import com.zwb.geekology.parser.lastfm.util.LastFmHelper;
import com.zwb.geekology.parser.lastfm.util.MyLogger;
import com.zwb.geekology.parser.lastfm.util.MyLogger.LogLevel;

import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Session;

public class GkParserLastFm extends AbstrGkParser implements IGkParser
{
	private MyLogger log = new MyLogger(this.getClass());

	public GkParserLastFm()
	{
		this.setSource("last.fm");

		log.debug("creating last.fm parser");
		String userAgent = Config.getUserAgent();
		boolean debugMode = Config.getDebugMode();
		String apiKey = Config.getApiKey();
		String secret = Config.getApiKeySecret();
		Session session;

		log.debug("configuring and authenticating last.fm access with userAgent=<"+userAgent+">, apiKey=<"+apiKey+">, secret=<"+secret+">, debugMode=<"+debugMode+">");
		Caller.getInstance().setUserAgent(userAgent);
	    Caller.getInstance().setDebugMode(debugMode);
	    session = Authenticator.getSession(Authenticator.getToken(apiKey), apiKey, secret);
	}
	
	@Override
	public IGkParsingResultArtist parseArtist(IGkParserQuery query) throws GkParserException
	{
		GkParsingResultArtist result = (GkParsingResultArtist) setResultStart(query, getSource());
		
		IGkDbArtist artist = null;
		if(query.isSampler())
		{
//			query for sampler
			log.debug("query for sampler "+query.getRelease());
			result.addEvent(GkParsingEventType.ERROR_ARGUMENT, "query for sampler with artist empty");
			result.setState(GkParsingState.ERROR);
			setResultErrorThrow(result);
		}
		if (query.hasRelease())
		{
			log.debug("query for artist <"+query.getArtist()+"> with release <"+query.getRelease()+">");
			artist = this.queryArtist(query.getArtist(), query.getRelease(), result);
		}
		else
		{
			log.debug("query for artist <"+query.getArtist()+">");
			artist = this.queryArtist(query.getArtist(), result);
		}
		result.setArtist(artist);
		return (IGkParsingResultArtist) setResultSuccess(result);			
	}

	@Override
	public IGkParsingResultSampler parseSampler(IGkParserQuery query) throws GkParserException
	{
		GkParsingResultSampler result = (GkParsingResultSampler) setResultStart(query, getSource());

		if(query.isSampler())
		{
//			query for sampler
			//TODO
			log.debug("query for sampler "+query.getRelease());
			throw new RuntimeException("NOT IMPLEMENTED YET!");
		}
		else
		{
			log.debug("query for artist <"+query.getArtist()+">");
			//TODO ERROR schmeissen!
			throw new RuntimeException("NOT IMPLEMENTED YET!");
		}
	}

	private IGkDbArtist queryArtist(String artistName, GkParsingResultArtist result) throws GkParserException
	{
		Collection<Artist> artists = queryLastFmArtists(artistName);
		if(!artists.isEmpty())
		{
			result.addEvent(GkParsingEventType.ENTRY_FOUND, "query for artist <"+artistName+"> returned <"+artists.size()+"> matches");
			return new GkDbArtistLastFm(artists.iterator().next());			
		}
		result.addEvent(GkParsingEventType.NO_ENTRY_FOUND, "query for artist <"+artistName+"> returned <"+artists.size()+"> matches");
		setResultErrorThrow(result);
		/** won't be reached */
		return null;
	}
	
	private IGkDbArtist queryArtist(String artistName, String releaseName, GkParsingResultArtist result) throws GkParserException
	{
		Artist artist = queryLastFmArtistViaReleases(artistName, releaseName);
		if(artist==null)
		{
			result.addEvent(GkParsingEventType.NO_ENTRY_FOUND, "release <"+releaseName+"> is NOT available for artist <"+artistName+">");
			return this.queryArtist(artistName, result);
		}
		result.addEvent(GkParsingEventType.ENTRY_FOUND, "release <"+releaseName+"> is available for artist <"+artistName+">");
		return new GkDbArtistLastFm(artist);
	}
	
	private IGkDbRelease querySampler(String samplerName)
	{
//		TODO
		throw new RuntimeException("NOT IMPLEMENTED YET!");
	}
	
	private Collection<Artist> queryLastFmArtists(String artistName)
	{
		Collection<Artist> artists = LastFmHelper.searchArtist(artistName, false);
		LogLevel level = LogLevel.DEBUG;
		if(log.isLogLevelEnabled(level))
		{
			List<String> matches = new ArrayList<>();
			Iterator<Artist> it = artists.iterator();
			while(it.hasNext())
			{
				matches.add(it.next().getName());
			}
			log.log(level, "query for artist <"+artistName+"> returned <"+artists.size()+"> matches: "+matches);
		}
		return artists;
	}
	
	private Artist queryLastFmArtistViaReleases(String artistName, String releaseName)
	{
		Collection<Album> albums = LastFmHelper.searchAlbum(releaseName, false);
		LogLevel level = LogLevel.DEBUG;
		List<String> matches = new ArrayList<>();
		if((albums==null)||(albums.size()==0))
		{
			return null;
		}
		Iterator<Album> it = albums.iterator();
		Artist ret = null;
		while(it.hasNext())
		{
			Album al = it.next();
			String ar = al.getArtist();
			matches.add(ar+"/"+al.getName());
			if(GkParserCommonUtils.compareReformatRemoveStartingThe(ar, artistName))
			{
				ret = LastFmHelper.searchArtist(ar, false).iterator().next();
				if(log.isLogLevelEnabled(level))
				{
					break;
				}
			}
		}
		log.log(level, "query for release <"+releaseName+"> returned <"+albums.size()+"> matches: "+matches);
		return ret;
	}

}
