package com.zwb.geekology.parser.lastfm.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.api.parser.IGkParsingEvent;
import com.zwb.geekology.parser.enums.GkParsingEventType;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.geekology.parser.lastfm.db.util.NameLoader;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;

import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.CallException;
import de.umass.lastfm.Tag;

public class GkDbArtistLastFm extends AbstrGkDbItemLastFmWithTags implements IGkDbArtist
{
	private Artist artist;
	private Ptr<List<IGkDbRelease>> releases = new Ptr<>();
	private Ptr<List<IGkDbArtist>> similar = new Ptr<>();
	private Ptr<List<String>> releaseNames = new Ptr<>();
	private Ptr<List<String>> similarsNames = new Ptr<>();
	
	public GkDbArtistLastFm(Artist artist)
	{
		super(artist, GkParserObjectFactory.createSource(Config.getSourceString()));
		this.artist = artist;
	}

	@Override
	public List<IGkDbRelease> getReleases() 
	{
		return LazyLoader.loadLazy(this.releases, new ReleaseLoader());
	}

	@Override
	public List<IGkDbTag> getStyleTags()
	{
		return LazyLoader.loadLazy(this.tags, new TagLoader());
	}

	@Override
	public List<IGkDbArtist> getSimilar()
	{
		return LazyLoader.loadLazy(this.similar, new SimilarLoader());
	}

	@Override
	public List<String> getReleaseNames() 
	{
		return LazyLoader.loadLazy(this.releaseNames, new NameLoader(this.getReleases()));
	}

	@Override
	public List<String> getSimilarsNames() 
	{
		return LazyLoader.loadLazy(this.similarsNames, new NameLoader(this.getSimilar()));
	}
	
	@Override
	public List<IGkParsingEvent> prefetch(List<IGkParsingEvent> events) 
	{
		try
		{
			this.getReleases();
		}
		catch(CallException e)
		{
			events.add(GkParserObjectFactory.createParsingEvent(GkParsingEventType.ATTRIBUTE_NOT_FOUND, "attribute <releases> of item <"+this.getName()+"> not found",  GkParserObjectFactory.createSource(Config.getSourceString())));
		}
		try
		{
			this.getSimilar();
		}
		catch(CallException e)
		{
			events.add(GkParserObjectFactory.createParsingEvent(GkParsingEventType.ATTRIBUTE_NOT_FOUND, "attribute <similar> of item <"+this.getName()+"> not found",  GkParserObjectFactory.createSource(Config.getSourceString())));
		}
		super.prefetch(events);
		return events;
	}

	class TagLoader implements ILoader
	{
		public List<IGkDbTag> load()
		{
			Collection<Tag> t = Artist.getTopTags(GkDbArtistLastFm.this.getName(), Config.getApiKey());
			Iterator<Tag> it = t.iterator();
			List<IGkDbTag> tags = new ArrayList<>();
			while(it.hasNext())
			{
				tags.add(new GkDbTagLastFm(it.next()));
			}
			return tags;
		}
	}
	
	class ReleaseLoader implements ILoader
	{
		@Override
		public List<IGkDbRelease> load() 
		{
			Collection<Album> query = Artist.getTopAlbums(GkDbArtistLastFm.this.artist.getName(), Config.getApiKey());
			List<IGkDbRelease> releases = new ArrayList<>();
			for(Album a: query)
			{
				releases.add(new GkDbReleaseLastFm(a, GkDbArtistLastFm.this));
			}
			return releases;
		}
	}
	
	class SimilarLoader implements ILoader
	{
		@Override
		public List<IGkDbArtist> load()
		{
			Collection<Artist> sim = Artist.getSimilar(GkDbArtistLastFm.this.artist.getName(), Config.getApiKey());
			List<IGkDbArtist> similar = new ArrayList<>();
			for(Artist a: sim)
			{
				similar.add(new GkDbArtistLastFm(a));
			}
			return similar;
		}
	}
	
	
}
