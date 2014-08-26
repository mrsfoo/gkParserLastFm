package com.zwb.geekology.parser.lastfm.db.util;

import java.util.ArrayList;
import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.lastfm.db.AbstrGkDbItemLastFmWithTags;
import com.zwb.lazyload.ILoader;

public class NameLoader implements ILoader
{
	List<? extends IGkDbItem> items;
	public NameLoader(List<? extends IGkDbItem> items)
	{
		this.items = items;
	}
	
	@Override
	public List<String> load()
	{
		List<String> tagNames = new ArrayList<>();
		for(IGkDbItem t: items)
		{
			tagNames.add(t.getName());
		}		
		return tagNames;
	}
}
