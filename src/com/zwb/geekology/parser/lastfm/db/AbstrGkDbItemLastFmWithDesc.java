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

public abstract class AbstrGkDbItemLastFmWithDesc extends AbstrGkDbItemLastFm implements IGkDbItemWithDesc
{
	private Ptr<String> summary = new Ptr<>();
	private Ptr<String> description = new Ptr<>();

	public AbstrGkDbItemLastFmWithDesc(MusicEntry lastfmMusicEntry, IGkParsingSource source)
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
	
	@Override
	public List<IGkParsingEvent> prefetch(List<IGkParsingEvent> events) 
	{
		try
		{
			this.getDescription();
		}
		catch(CallException e)
		{
			events.add(GkParserObjectFactory.createParsingEvent(GkParsingEventType.ATTRIBUTE_NOT_FOUND, "attribute <description> of item <"+this.getName()+"> not found",  GkParserObjectFactory.createSource(Config.getSourceString())));
		}
		try
		{
			this.getDescriptionSummary();
		}
		catch(CallException e)
		{
			events.add(GkParserObjectFactory.createParsingEvent(GkParsingEventType.ATTRIBUTE_NOT_FOUND, "attribute <summary> of item <"+this.getName()+"> not found",  GkParserObjectFactory.createSource(Config.getSourceString())));
		}
		super.prefetch(events);
		return events;
	}

	class SummaryLoader implements ILoader<String>
	{
		@Override
		public String load()
		{
			return AbstrGkDbItemLastFmWithDesc.this.lastfmMusicEntry.getWikiSummary();
		}
	}
	
	class DescLoader implements ILoader<String>
	{
		@Override
		public String load()
		{
			return AbstrGkDbItemLastFmWithDesc.this.lastfmMusicEntry.getWikiText();
		}
	}
}
