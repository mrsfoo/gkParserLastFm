package com.zwb.geekology.parser.lastfm.db;

import java.util.List;

import com.zwb.geekology.parser.api.db.IGkDbItemWithDesc;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.api.parser.IGkParsingEvent;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.geekology.parser.enums.GkParsingEventType;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;

import de.umass.lastfm.CallException;
import de.umass.lastfm.MusicEntry;

public abstract class AbstrGkDbItemLastFmWithDesc extends AbstrGkDbItemLastFm
	implements IGkDbItemWithDesc
{
    private Ptr<String> summary = new Ptr<>();
    private Ptr<String> description = new Ptr<>();
    
    public AbstrGkDbItemLastFmWithDesc(MusicEntry lastfmMusicEntry,
	    IGkParsingSource source)
    {
	super(lastfmMusicEntry, source);
    }
    
    @Override
    public String getDescriptionSummary()
    {
	return LazyLoader.loadLazy(this.summary, new SummaryLoader());
    }
    
    @Override
    public String getDescription()
    {
	return LazyLoader.loadLazy(this.description, new DescLoader());
    }
    
    class SummaryLoader implements ILoader<String>
    {
	@Override
	public String load()
	{
	    return AbstrGkDbItemLastFmWithDesc.this.lastfmMusicEntry
		    .getWikiSummary();
	}
    }
    
    class DescLoader implements ILoader<String>
    {
	@Override
	public String load()
	{
	    return AbstrGkDbItemLastFmWithDesc.this.lastfmMusicEntry
		    .getWikiText();
	}
    }
}
