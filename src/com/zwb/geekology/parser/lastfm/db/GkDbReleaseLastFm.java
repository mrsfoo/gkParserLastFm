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
import com.zwb.geekology.parser.lastfm.db.GkDbTrackLastFm.TagLoader;
import com.zwb.geekology.parser.lastfm.util.LastFmHelper;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;

import de.umass.lastfm.Album;
import de.umass.lastfm.Tag;
import de.umass.lastfm.Track;

public class GkDbReleaseLastFm extends AbstrGkDbItemLastFmWithTags implements IGkDbRelease
{
	private IGkDbArtist artist;
	private Album album;
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
		return LazyLoader.loadLazy(this.tags, new TagLoader());
	}

	public List<IGkDbTrack> getTracks() 
	{
		return LazyLoader.loadLazy(this.tags, new TrackLoader());
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
	
	class TrackLoader implements ILoader
	{
		public List<IGkDbTrack> load()
		{
			Collection<Track> t = GkDbReleaseLastFm.this.album.getTracks();
			Iterator<Track> it = t.iterator();
			List<IGkDbTrack> tracks = new ArrayList<>();
			int i = 1;
			while(it.hasNext())
			{
				tracks.add(new GkDbTrackLastFm(it.next(), GkDbReleaseLastFm.this.getArtist(), GkDbReleaseLastFm.this, i));
				i++;
			}
			return tracks;
		}
	}

	class TagLoader implements ILoader
	{
		public List<IGkDbTag> load()
		{
			Collection<Tag> t = Album.getTopTags(GkDbReleaseLastFm.this.getArtist().getName(), GkDbReleaseLastFm.this.getName(), Config.getApiKey());
			Iterator<Tag> it = t.iterator();
			List<IGkDbTag> tags = new ArrayList<>();
			while(it.hasNext())
			{
				tags.add(new GkDbTagLastFm(it.next()));
			}
			return tags;
		}
	}

}
