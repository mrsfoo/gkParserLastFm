package com.zwb.geekology.parser.lastfm.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.api.parser.IGkParsingEvent;
import com.zwb.geekology.parser.enums.GkParsingEventType;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.geekology.parser.lastfm.db.util.NameLoader;
import com.zwb.geekology.parser.lastfm.util.LastFmHelper;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;

import de.umass.lastfm.CallException;
import de.umass.lastfm.Tag;

public class GkDbTagLastFm extends AbstrGkDbItem implements IGkDbTag
{
	private Tag tag;
	private Ptr<String> summary = new Ptr<>();
	private Ptr<String> description = new Ptr<>();
	private Ptr<List<IGkDbTag>> similar = new Ptr<>();
	private Ptr<List<String>> similarNames = new Ptr<>();
	private Ptr<Double> weight = new Ptr<>();
	
	public GkDbTagLastFm(Tag tag)
	{
		super(tag.getName(), GkParserObjectFactory.createSource(Config.getSourceString()));
		this.tag = tag;
	}

	@Override
	public double getWeight()
	{
		return LazyLoader.loadLazy(this.weight, new WeightLoader());
	}

	@Override
	public List<IGkDbTag> getSimilar()
	{
		return LazyLoader.loadLazy(this.similar, new SimilarLoader());
	}

	@Override
	public List<String> getSimilarsNames() 
	{
		return LazyLoader.loadLazy(this.similarNames, new NameLoader(this.getSimilar()));
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
			return GkDbTagLastFm.this.tag.getWikiSummary();
		}
	}
	
	class DescLoader implements ILoader<String>
	{
		@Override
		public String load()
		{
			return GkDbTagLastFm.this.tag.getWikiText();
		}
	}
	
	class WeightLoader implements ILoader<Double>
	{
		@Override
		public Double load()
		{
			return new Double(GkDbTagLastFm.this.tag.getCount());
		}
	}
	
	class SimilarLoader implements ILoader
	{
		@Override
		public List<IGkDbTag> load()
		{
			Collection<Tag> sim = Tag.getSimilar(GkDbTagLastFm.this.tag.getName(), Config.getApiKey());
			List<IGkDbTag> similar = new ArrayList<>();
			for(Tag t: sim)
			{
				similar.add(new GkDbTagLastFm(t));
			}
			return similar;
		}
	}

}
