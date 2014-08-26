package com.zwb.geekology.parser.lastfm.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.geekology.parser.lastfm.util.LastFmHelper;

import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.MusicEntry;
import de.umass.lastfm.Tag;

public class GkDbArtistLastFm extends AbstrGkDbItemLastFmWithTags implements IGkDbArtist
{
	private Artist artist;
	private List<IGkDbRelease> releases;
	private List<IGkDbArtist> similar;
	private List<String> releaseNames;
	private List<String> similarsNames;
	
	public GkDbArtistLastFm(Artist artist)
	{
		super(artist, GkParserObjectFactory.createSource(Config.getSourceString()));
		this.artist = artist;
	}

	@Override
	public List<IGkDbRelease> getReleases() 
	{
		if(this.releases==null)
		{
			this.releases = new ArrayList<>();
			Collection<Album> albums = LastFmHelper.searchAlbumsForArtist(this.getName(), true);
			Iterator<Album> it = albums.iterator();
			while(it.hasNext())
			{
				this.releases.add(new GkDbReleaseLastFm(it.next(), this));
			}
		}
		return this.releases;
	}

	@Override
	public List<IGkDbTag> getStyleTags()
	{
		if(this.tags==null)
		{
			this.tags = new ArrayList<>();
			Collection<Tag> lastFmTags = LastFmHelper.searchTagsForArtist(this.getName(), true);
			Iterator<Tag> it = lastFmTags.iterator();
			while(it.hasNext())
			{
				tags.add(new GkDbTagLastFm(it.next()));
			}
		}
		return this.tags;
	}

	@Override
	public List<IGkDbArtist> getSimilar()
	{
		if(this.similar == null)
		{
			this.similar = new ArrayList<>();
			Collection<Artist> lfma = LastFmHelper.searchSimilarArtist(this.getName(), true);
			Iterator<Artist> it = lfma.iterator();
			while(it.hasNext())
			{
				this.similar.add(new GkDbArtistLastFm(it.next()));
			}
		}
		return similar;
	}

	@Override
	public List<String> getReleaseNames() 
	{
		if(this.releaseNames==null)
		{
			this.releaseNames = new ArrayList<>();
			for(IGkDbRelease r: this.getReleases())
			{
				this.releaseNames.add(r.getName());
			}
		}
		return this.releaseNames;
	}

	@Override
	public List<String> getSimilarsNames() 
	{
		if(this.similarsNames==null)
		{
			this.similarsNames = new ArrayList<>();
			for(IGkDbArtist a: this.getSimilar())
			{
				this.similarsNames.add(a.getName());
			}
		}
		return this.similarsNames;
	}
}
