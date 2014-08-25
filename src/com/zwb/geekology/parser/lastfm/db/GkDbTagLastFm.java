package com.zwb.geekology.parser.lastfm.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.geekology.parser.lastfm.util.LastFmHelper;

import de.umass.lastfm.Tag;

public class GkDbTagLastFm extends AbstrGkDbItem implements IGkDbTag
{
	private Tag tag;
	private String summary;
	private String description;
	private List<IGkDbTag> similar;
	private List<String> similarNames;
	
	
	public GkDbTagLastFm(Tag tag)
	{
		super(tag.getName(), GkParserObjectFactory.createSource(Config.getSourceString()));
		this.tag = tag;
	}

	@Override
	public double getWeight()
	{
		return this.tag.getCount();
	}

	@Override
	public String getDescriptionSummary()
	{
		if(this.summary==null)
		{
			this.summary = this.tag.getWikiSummary();
		}
		return this.summary;
	}

	@Override
	public String getDescription() 
	{
		if(this.description==null)
		{
			this.description = this.tag.getWikiText();
		}
		return this.description;
	}

	@Override
	public List<IGkDbTag> getSimilar()
	{
		if(this.similar==null)
		{
			this.similar = new ArrayList<>();
			Collection<Tag> lfmt = LastFmHelper.searchSimilarTags(this.getName(), true);
			for(Tag t: lfmt)
			{
				this.similar.add(new GkDbTagLastFm(t));
			}
		}
		return this.similar;
	}

	@Override
	public List<String> getSimilarsNames() 
	{
		if(this.similarNames==null)
		{
			this.similarNames = new ArrayList<>();
			for(IGkDbTag t: this.getSimilar())
			{
				this.similarNames.add(t.getName());
			}
		}
		return this.similarNames;
	}
}
