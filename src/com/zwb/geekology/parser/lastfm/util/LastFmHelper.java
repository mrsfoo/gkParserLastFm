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
	
	public static Collection<Tag> searchTagsForArtist(String artistName, boolean catchExceptions)
	{
		try
		{
			return Artist.getTopTags(artistName, Config.getApiKey());
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
	
	public static Collection<Artist> searchSimilarArtist(String artistName, boolean catchExceptions)
	{
		try
		{
			return Artist.getSimilar(artistName, Config.getApiKey());
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
	
	public static Collection<Album> searchAlbum(String albumName, boolean catchExceptions)
	{
		try
		{
			return Album.search(albumName, Config.getApiKey());
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
	
	public static Collection<Tag> searchSimilarTags(String tagName, boolean catchExceptions)
	{
		try
		{
			return Tag.getSimilar(tagName, Config.getApiKey());
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

	public static Collection<Tag> searchTagsForAlbum(String artistName, String albumName, boolean catchExceptions)
	{
		try
		{
			return Album.getTopTags(artistName, albumName, Config.getApiKey());
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
	
	public static Collection<Track> searchTracksForAlbum(Album album, boolean catchExceptions)
	{
		try
		{
			return album.getTracks();
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

	public static Date searchDateForAlbum(Album album, boolean catchExceptions)
	{
		try
		{
			return album.getReleaseDate();
		}
		catch (CallException e)
		{
			if(catchExceptions)
			{
				return null;
			}
			else
			{
				throw e;
			}
		}
	}	

}
