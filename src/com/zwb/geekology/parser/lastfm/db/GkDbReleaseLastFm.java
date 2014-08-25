package com.zwb.geekology.parser.lastfm.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.db.IGkDbTrack;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.geekology.parser.lastfm.util.LastFmHelper;

import de.umass.lastfm.Album;
import de.umass.lastfm.Tag;
import de.umass.lastfm.Track;

public class GkDbReleaseLastFm extends AbstrGkDbItemLastFmWithDesc implements IGkDbRelease
{
	private IGkDbArtist artist;
	private Album album;
	private List<IGkDbTag> tags;
	private List<String> tagNames;
	private List<IGkDbTrack> tracks;
	private List<String> trackNames;
	private Date date;
	
	public GkDbReleaseLastFm(Album album)
	{
		super(album, GkParserObjectFactory.createSource(Config.getSourceString()));
		this.album = album;
		throw new RuntimeException("NOT IMPLEMENTED YET!");
	}

	public GkDbReleaseLastFm(Album album, IGkDbArtist artist)
	{
		super(album, GkParserObjectFactory.createSource(Config.getSourceString()));
		this.album = album;
		this.artist = artist;
		this.album.getReleaseDate();
		this.album.getTracks();
		this.album.getTags();
	}

	@Override
	public boolean isSampler()
	{
		//TODO
		throw new RuntimeException("NOT IMPLEMENTED YET!");
	}

	@Override
	public IGkDbArtist getArtist()
	{
		return this.artist;
	}

	@Override
	public List<IGkDbTag> getStyleTags()
	{
		if(this.tags==null)
		{
			this.tags = new ArrayList<>();
			Collection<Tag> lastFmTags = LastFmHelper.searchTagsForAlbum(this.getArtist().getName(), this.getName(), true);
			Iterator<Tag> it = lastFmTags.iterator();
			while(it.hasNext())
			{
				tags.add(new GkDbTagLastFm(it.next()));
			}
		}
		return this.tags;
	}

	@Override
	public List<String> getStyleTagNames() 
	{
		if(this.tagNames==null)
		{
			this.tagNames = new ArrayList<>();
			for(IGkDbTag t: this.getStyleTags())
			{
				this.tagNames.add(t.getName());
			}			
		}
		return this.tagNames;
	}

	public List<IGkDbTrack> getTracks() 
	{
		if(this.tracks==null)
		{
			this.tracks = new ArrayList<>();
			Collection<Track> lfmts = LastFmHelper.searchTracksForAlbum(this.album, true);
			for(Track t: lfmts)
			{
				this.tracks.add(new GkDbTrack(t, this.getArtist(), this));
			}
		}
		return this.tracks;
	}

	@Override
	public List<String> getTrackNames() 
	{
		if(this.trackNames==null)
		{
			this.trackNames = new ArrayList<>();
			for(IGkDbTrack t: this.getTracks())
			{
				this.trackNames.add(t.getName());
			}
		}
		return this.trackNames;
	}

	@Override
	public Date getReleaseDate() 
	{
		if(this.date==null)
		{
			this.date = LastFmHelper.searchDateForAlbum(this.album, true);
		}
		return this.date;
	}
}
