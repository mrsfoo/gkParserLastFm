package com.zwb.geekology.parser.lastfm.junit;

import java.util.Collection;

import junit.framework.TestCase;

import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.geekology.parser.lastfm.util.SessionManager;

import de.umass.lastfm.Album;
import de.umass.lastfm.Library;
import de.umass.lastfm.Track;

public class FrameworkTestLastFm extends TestCase
{
    public void testGetTracks()
    {
	SessionManager sm = SessionManager.getInstance();
	Collection<Album> albums = Album.search("diamond dogs", Config.getApiKey());
	Album album = null;
	System.out.println("album search results:");
	for (Album a : albums)
	{
	    System.out.println("* <" + a.getName() + "> by <" + a.getArtist() + ">");
	    if ((album == null) && a.getArtist().equals("David Bowie"))
	    {
		album = a;
	    }
	}
	Collection<Track> tracks = album.getTracks();
	System.out.println("\ntracks of <" + album.getName() + "> by <" + album.getArtist() + ">:");
	if (tracks != null)
	{
	    for (Track t : tracks)
	    {
		System.out.println("* <" + t.getName() + "> by <" + t.getArtist() + "> on <" + t.getAlbum() + ">");
	    }
	}
	else
	{
	    System.out.println("* " + tracks);
	}
	
	System.out.println("session params:");
	System.out.println("API key  : "+sm.getSession().getApiKey());
	System.out.println("key      : "+sm.getSession().getKey());
	System.out.println("secret   : "+sm.getSession().getSecret());
	System.out.println("username : "+sm.getSession().getUsername());
	
	Library.addAlbum("David Bowie", "Diamond Dogs", sm.getSession());
	tracks = Library.getAllTracks(sm.getSession().getUsername(), Config.getApiKey());
	System.out.println("\ntracks of <" + album.getName() + "> by <" + album.getArtist() + ">:");
	if (tracks != null)
	{
	    for (Track t : tracks)
	    {
		System.out.println("* <" + t.getName() + "> by <" + t.getArtist() + "> on <" + t.getAlbum() + ">");
	    }
	}
	else
	{
	    System.out.println("* " + tracks);
	}
	
    }
    
}
