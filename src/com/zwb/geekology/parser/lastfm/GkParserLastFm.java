package com.zwb.geekology.parser.lastfm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkParser;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.exception.GkParserException;
import com.zwb.geekology.parser.api.parser.IGkParser;
import com.zwb.geekology.parser.api.parser.IGkParserQuery;
import com.zwb.geekology.parser.api.parser.IGkParsingResultArtist;
import com.zwb.geekology.parser.api.parser.IGkParsingResultSampler;
import com.zwb.geekology.parser.enums.GkParsingEventType;
import com.zwb.geekology.parser.enums.GkParsingState;
import com.zwb.geekology.parser.impl.GkParsingResultArtist;
import com.zwb.geekology.parser.impl.GkParsingResultSampler;
import com.zwb.geekology.parser.lastfm.db.GkDbArtistLastFm;
import com.zwb.geekology.parser.lastfm.util.LastFmHelper;
import com.zwb.geekology.parser.lastfm.util.MyLogger;
import com.zwb.geekology.parser.lastfm.util.MyLogger.LogLevel;
import com.zwb.geekology.parser.lastfm.util.SessionManager;
import com.zwb.geekology.parser.lastfm.util.StringUtilsLastFm;
import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.CallException;

public class GkParserLastFm extends AbstrGkParser implements IGkParser
{
    private MyLogger log = new MyLogger(this.getClass());
    private LastFmHelper lastFm;
    
    public GkParserLastFm()
    {
	super();
	try
	{
	    log.debug("creating last.fm parser");
	    this.setSource(Config.getSourceString());
	    this.lastFm = new LastFmHelper();
	    SessionManager.getInstance();
	}
	catch (CallException e)
	{
	    setConstructorEvent(GkParsingEventType.EXTERNAL_ERROR, "exception in last.fm framework; probably bad internet connection: " + e.getClass().getName() + " -- " + e.getMessage());
	}
    }
    
    @Override
    public IGkParsingResultArtist parseArtist(IGkParserQuery query) throws GkParserException
    {
	GkParsingResultArtist result = (GkParsingResultArtist) setResultStart(query, getSource());
	IGkDbArtist artist = null;
	try
	{
	    if (query.isSampler())
	    {
		// query for sampler
		log.debug("query for sampler " + query.getRelease());
		result.addEvent(GkParsingEventType.ERROR_ARGUMENT, "query for sampler with artist empty");
		result.setState(GkParsingState.ERROR);
		setResultErrorThrow(result, null);
	    }
	    if (query.hasRelease())
	    {
		log.debug("query for artist <" + query.getArtist() + "> with release <" + query.getRelease() + ">");
		artist = this.queryArtistViaRelease(query.getArtist(), query.getRelease(), result);
	    }
	    else
	    {
		log.debug("query for artist <" + query.getArtist() + ">");
		artist = this.queryArtist(query.getArtist(), result);
	    }
	    result.setArtist(artist);
	    return (IGkParsingResultArtist) setResultSuccess(result);
	}
	catch (CallException e)
	{
	    result.addEvent(GkParsingEventType.EXTERNAL_ERROR, "exception in last.fm framework; probably bad internet connection: " + e.getClass().getName() + " -- " + e.getMessage());
	    this.setResultErrorThrow(result, e);
	}
	return null;
    }
    
    @Override
    public IGkParsingResultSampler parseSampler(IGkParserQuery query) throws GkParserException
    {
	GkParsingResultSampler result = (GkParsingResultSampler) setResultStart(query, getSource());
	
	if (query.isSampler())
	{
	    // query for sampler
	    // TODO
	    log.debug("query for sampler " + query.getRelease());
	    throw new RuntimeException("NOT IMPLEMENTED YET!");
	}
	else
	{
	    log.debug("query for artist <" + query.getArtist() + ">");
	    // TODO ERROR schmeissen!
	    throw new RuntimeException("NOT IMPLEMENTED YET!");
	}
    }
    
    private IGkDbArtist queryArtist(String artistName, GkParsingResultArtist result) throws GkParserException
    {
	log.debug("QUERY: query artist <" + artistName + ">");
	Collection<Artist> artists = queryLastFmArtists(artistName);
	if (!artists.isEmpty())
	{
	    result.addEvent(GkParsingEventType.ENTRY_FOUND, "query for artist <" + artistName + "> returned <" + artists.size() + "> matches");
	    Artist chosen = findBestMatchingArtist(artistName, artists);
	    log.debug("QUERY: queried artist <" + artistName + ">: " + chosen);
	    return new GkDbArtistLastFm(chosen);
	}
	log.debug("QUERY: no result for artist query <" + artistName + ">");
	result.addEvent(GkParsingEventType.NO_ENTRY_FOUND, "query for artist <" + artistName + "> returned <" + artists.size() + "> matches");
	setResultErrorThrow(result, null);
	/** won't be reached */
	return null;
    }
    
    private IGkDbArtist queryArtistViaRelease(String artistName, String releaseName, GkParsingResultArtist result) throws GkParserException
    {
	log.debug("QUERY: query artist <" + artistName + "> via release <" + releaseName + ">");
	Artist artist = queryLastFmArtistViaReleases(artistName, releaseName);
	if (artist == null)
	{
	    result.addEvent(GkParsingEventType.NO_ENTRY_FOUND, "release <" + releaseName + "> is NOT available for artist <" + artistName + ">");
	    log.debug("QUERY: no result for artist query <" + artistName + "> via release <" + releaseName + ">, trying query without release");
	    return this.queryArtist(artistName, result);
	}
	log.debug("QUERY: queried artist <" + artistName + "> via release <" + releaseName + ">: " + artist);
	result.addEvent(GkParsingEventType.ENTRY_FOUND, "release <" + releaseName + "> is available for artist <" + artistName + ">");
	return new GkDbArtistLastFm(artist);
    }
    
    private IGkDbRelease querySampler(String samplerName)
    {
	// TODO
	throw new RuntimeException("NOT IMPLEMENTED YET!");
    }
    
    private Collection<Artist> queryLastFmArtists(String artistName)
    {
	log.debug("QUERY-LAST.FM: query last.fm artist <" + artistName + ">");
	Collection<Artist> artists = this.lastFm.searchArtist(artistName, false);
	logArtistList(LogLevel.DEBUG, artists, "QUERY-LAST.FM: query for artist <" + artistName + "> returned:", "QUERY-LAST.FM: ");
	return artists;
    }
    
    private Artist queryLastFmArtistViaReleases(String artistName, String releaseName)
    {
	log.debug("QUERY-LAST.FM: query last.fm artist <" + artistName + "> via release <" + releaseName + ">");
	Collection<Album> albums = this.lastFm.searchAlbum(artistName, false);
	if ((albums == null) || (albums.size() == 0))
	{
	    log.debug("QUERY-LAST.FM: query for last.fm artist <" + artistName + "> via release <" + releaseName + "> returned no result");
	    return null;
	}
	logReleaseList(LogLevel.DEBUG, albums, "QUERY-LAST.FM: query for artist <" + artistName + "> via release <" + releaseName + "> returned:", "QUERY-LAST.FM: ");
	Artist a = findBestMatchingAlbumArtist(artistName, releaseName, albums);
	log.debug("QUERY-LAST.FM: found best matching artist for query for artist <" + artistName + "> via release <" + releaseName + ">: " + a);
	return a;
    }
    
    private Artist findBestMatchingArtist(String artistName, Collection<Artist> artists)
    {
	Iterator<Artist> it = artists.iterator();
	int i = 0;
	double thresh = Config.getSearchTreshold();
	log.trace("MATCH: find best matching artist; name=<" + artistName + ">, thresh=<" + thresh + ">, from: <" + artists + ">");
	while (it.hasNext())
	{
	    if (i >= Config.getSearchDepth())
	    {
		log.warn("MATCH: search depth [" + i + "/" + artists.size() + "] reached, break up!");
		break;
	    }
	    Artist me = it.next();
	    String meName = me.getName();
	    double val = StringUtilsLastFm.compareArtists(meName, artistName);
	    log.trace("MATCH: comparing: <" + artistName + "> with <" + meName + "> --> [" + val + ">=" + thresh + "]?");
	    if (val >= thresh)
	    {
		log.info("MATCH: match <" + artistName + "> ~~ <" + meName + ">!");
		return me;
	    }
	}
	return artists.iterator().next();
    }
    
    private Artist findBestMatchingAlbumArtist(String artistName, String releaseName, Collection<Album> albums)
    {
	List<String> matches = new ArrayList<>();
	Artist ret = null;
	Iterator<Album> it = albums.iterator();
	LogLevel level = LogLevel.DEBUG;
	double thresh = Config.getSearchTresholdViaAlbum();
	log.trace("MATCH: find best matching album artist; artist name=<" + artistName + ">, release name=<" + releaseName + ">, thresh=<" + thresh + ">, from: <" + albums + ">");
	int i = 0;
	while (it.hasNext())
	{
	    Album albumLocal = it.next();
	    String artistNameLocal = albumLocal.getArtist();
	    matches.add(artistNameLocal + "/" + albumLocal.getName());
	    if (i >= Config.getSearchDepth())
	    {
		log.warn("MATCH: search depth [" + i + "/" + albums.size() + "] reached, break up!");
		break;
	    }
	    
	    double val = StringUtilsLastFm.compareArtists(artistNameLocal, artistName);
	    log.trace("MATCH: comparing: <" + artistName + "> with <" + artistNameLocal + "> --> [" + val + ">=" + thresh + "]?");
	    if (val >= thresh)
	    {
		log.info("MATCH: match <" + artistName + "> ~~ <" + artistNameLocal + ">!");
		ret = this.lastFm.searchArtist(artistNameLocal, false).iterator().next();
		if (log.isLogLevelEnabled(level))
		{
		    break;
		}
	    }
	}
	log.log(level, "MATCH: query for release <" + releaseName + "> returned <" + albums.size() + "> matches: " + matches);
	return ret;
    }
    
    private void logArtistList(LogLevel level, Collection<Artist> list, String headline, String prefix)
    {
	if (log.isLogLevelEnabled(level))
	{
	    log.log(level, headline);
	    log.log(level, "(size is [" + list.size() + "])");
	    Iterator<Artist> it = list.iterator();
	    while (it.hasNext())
	    {
		log.log(level, "  " + prefix + it.next().getName());
	    }
	}
    }
    
    private void logReleaseList(LogLevel level, Collection<Album> list, String headline, String prefix)
    {
	if (log.isLogLevelEnabled(level))
	{
	    log.log(level, headline);
	    log.log(level, "(size is [" + list.size() + "])");
	    Iterator<Album> it = list.iterator();
	    while (it.hasNext())
	    {
		log.log(level, "  " + prefix + it.next().getName());
	    }
	}
    }
    
}
