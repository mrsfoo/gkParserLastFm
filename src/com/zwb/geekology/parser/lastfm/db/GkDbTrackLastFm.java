package com.zwb.geekology.parser.lastfm.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.db.IGkDbTrack;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.api.parser.IGkParsingEvent;
import com.zwb.geekology.parser.enums.GkParsingEventType;
import com.zwb.geekology.parser.impl.util.DbItemFormatter;
import com.zwb.geekology.parser.impl.util.GkParserStringUtils;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.geekology.parser.lastfm.util.StringUtilsLastFm;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;
import com.zwb.stringutil.ISatiniseFilter;
import com.zwb.stringutil.ISatiniseFilterArray;
import com.zwb.tab.Tab;

import de.umass.lastfm.CallException;
import de.umass.lastfm.Tag;
import de.umass.lastfm.Track;

public class GkDbTrackLastFm extends AbstrGkDbItemLastFmWithTags implements IGkDbTrack
{
    private Track track;
    private IGkDbRelease release;
    private IGkDbArtist artist;
    private Integer trackNo;
    private Ptr<Long> duration = new Ptr<Long>();
    
    public GkDbTrackLastFm(Track track, IGkDbArtist artist, IGkDbRelease release, int trackNo)
    {
	super(track, GkParserObjectFactory.createSource(Config.getSourceString()));
	this.track = track;
	this.artist = artist;
	this.release = release;
	this.trackNo = trackNo;
    }
    
    @Override
    public Integer getTrackNo()
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
    public Long getDuration()
    {
	try
	{
	    return LazyLoader.loadLazy(this.duration, new DurationLoader());
	}
	catch (CallException e)
	{
	    this.addEvent(GkParserObjectFactory.createParsingEvent(GkParsingEventType.EXTERNAL_ERROR, "exception in last.fm framework while loading duration of track <" + this.getName() + ">; probably bad internet connection: " + e.getClass().getName() + " -- " + e.getMessage(), this.getSource()));
	    return null;
	}
    }
    
    @Override
    public List<IGkDbTag> getStyleTags()
    {
	try
	{
	    return LazyLoader.loadLazy(this.tags, new TagLoader());
	}
	catch (CallException e)
	{
	    this.addEvent(GkParserObjectFactory.createParsingEvent(GkParsingEventType.EXTERNAL_ERROR, "exception in last.fm framework while loading style tags of track <" + this.getName() + ">; probably bad internet connection: " + e.getClass().getName() + " -- " + e.getMessage(), this.getSource()));
	    return null;
	}
    }
    
    @Override
    public boolean hasDuration()
    {
	return (this.getDuration() != -1);
    }
    
    class DurationLoader implements ILoader<Long>
    {
	public Long load()
	{
	    return new Long(GkDbTrackLastFm.this.track.getDuration());
	}
    }
    
    class TagLoader implements ILoader
    {
	public List<IGkDbTag> load()
	{
	    Collection<Tag> t = Track.getTopTags(GkDbTrackLastFm.this.getArtist().getName(), GkDbTrackLastFm.this.getName(), Config.getApiKey());
	    Iterator<Tag> it = t.iterator();
	    List<IGkDbTag> tags = new ArrayList<>();
	    while (it.hasNext())
	    {
		tags.add(new GkDbTagLastFm(it.next()));
	    }
	    return tags;
	}
    }

    @Override
    public Integer getDiscNo()
    {
	return 1;
    }
    
    @Override
    public int compareTo(IGkDbTrack o)
    {
	if(this.getAbsolutePosition()>o.getAbsolutePosition())
	{
	    return 1;
	}
	else if(this.getAbsolutePosition()<o.getAbsolutePosition())
	{
	    return -1;
	}
	else
	{
	    return this.getName().compareTo(o.getName());
	}
    }

    @Override
    public Integer getAbsolutePosition()
    {
	Map<Integer, Integer> trackNos = new HashMap<Integer,Integer>();
	
	int abs = 0;
	for(int i=1; i<this.getDiscNo(); i++)
	{
	    abs += trackNos.get(i);
	}
	abs += this.getTrackNo();
	return abs;
    }

    @Override
    public ISatiniseFilterArray getFilters()
    {
	return StringUtilsLastFm.getAllTrackNameFilters();
    }
}
