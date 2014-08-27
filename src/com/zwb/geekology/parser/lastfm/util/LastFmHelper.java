package com.zwb.geekology.parser.lastfm.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.zwb.config.api.ConfigurationFactory;
import com.zwb.config.api.IConfiguration;
import com.zwb.geekology.parser.lastfm.Config;

import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.CallException;
import de.umass.lastfm.MusicEntry;
import de.umass.lastfm.Tag;
import de.umass.lastfm.Track;

public class LastFmHelper 
{
	public static Collection<Artist> searchArtist(String artistName, boolean catchExceptions)
	{
		try
		{
			return Artist.search(artistName, Config.getApiKey());
		}
		catch (CallException e)
		{
			if(catchExceptions)
			{
				return Collections.emptyList();
			}
			else
			{
				throw e;
			}
		}
	}
	
	public static Collection<Album> searchAlbumsForArtist(String artistName, boolean catchExceptions)
	{
		try
		{
			return Artist.getTopAlbums(artistName, Config.getApiKey());
		}
		catch (CallException e)
		{
			if(catchExceptions)
			{
				return Collections.emptyList();
			}
			else
			{
				throw e;
			}
		}
	}
}
