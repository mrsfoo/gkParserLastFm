package com.zwb.geekology.parser.lastfm.db;

import java.util.ArrayList;
import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbItemWithDesc;
import com.zwb.geekology.parser.api.db.IGkDbItemWithStyleTags;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;

import de.umass.lastfm.MusicEntry;

public abstract class AbstrGkDbItemLastFmWithTags extends AbstrGkDbItemLastFmWithDesc implements IGkDbItemWithStyleTags
{
	protected List<IGkDbTag> tags;
	protected Ptr<List<String>> tagNames = new Ptr<>();

	public AbstrGkDbItemLastFmWithTags(MusicEntry lastfmMusicEntry, IGkParsingSource source)
	{
		super(lastfmMusicEntry, source);
	}

	@Override
	public List<String> getStyleTagNames() 
	{
		return LazyLoader.loadLazy(this.tagNames, new TagNameLoader(this.tags));
	}

	@Override
	public abstract List<IGkDbTag> getStyleTags();


}

class TagNameLoader implements ILoader
{
	List<IGkDbTag> tags;
	
	public TagNameLoader(List<IGkDbTag> tags)
	{
		this.tags = tags;
	}
	
	@Override
	public List<String> load()
	{
		List<String> tagNames = new ArrayList<>();
		for(IGkDbTag t: this.tags)
		{
			tagNames.add(t.getName());
		}		
		return tagNames;
	}
	
}