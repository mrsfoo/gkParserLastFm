package com.zwb.geekology.parser.lastfm.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.db.IGkDbTrack;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;

import de.umass.lastfm.Tag;
import de.umass.lastfm.Track;

public class GkDbTrack extends AbstrGkDbItemLastFmWithTags implements IGkDbTrack
{
	private Track track;
	private IGkDbRelease release;
	private IGkDbArtist artist;
	private int trackNo;
	private Ptr<Integer> duration = new Ptr<Integer>();
	
	public GkDbTrack(Track track, IGkDbArtist artist, IGkDbRelease release, int trackNo)
	{
		super(track, GkParserObjectFactory.createSource(Config.getSourceString()));
		this.track = track;
		this.artist = artist;
		this.release = release;
		this.trackNo = trackNo;
		
		this.track.getLastFmInfo(Config.getApiKey());
		this.track.getLocation();
	}
	
	@Override
	public int getTrackNo()
	{
		return this.trackNo;
	}

	@Override
	public IGkDbRelease getRelease() 
	{
		return this.release;
	}

	@Override
	public IGkDbArtist getArtist()
	{
		return this.artist;
	}

	@Override
	public int getDuration()
	{
		return LazyLoader.loadLazy(this.duration, new DurationLoader());
	}

	@Override
	public List<IGkDbTag> getStyleTags()
	{
		return LazyLoader.loadLazy(this.tags, new TagLoader());
	}

	class DurationLoader implements ILoader<Integer>
	{
		public Integer load()
		{
			return GkDbTrack.this.track.getDuration();
		}
	}

	class TagLoader implements ILoader
	{
		public List<IGkDbTag> load()
		{
			Collection<Tag> t = Track.getTopTags(GkDbTrack.this.getArtist().getName(), GkDbTrack.this.getName(), Config.getApiKey());
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

