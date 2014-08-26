package com.zwb.geekology.parser.lastfm.db;

import java.util.ArrayList;
import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbItemWithDesc;
import com.zwb.geekology.parser.api.db.IGkDbItemWithStyleTags;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.geekology.parser.lastfm.db.util.NameLoader;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;

import de.umass.lastfm.MusicEntry;

public abstract class AbstrGkDbItemLastFmWithTags extends AbstrGkDbItemLastFmWithDesc implements IGkDbItemWithStyleTags
{
	protected Ptr<List<IGkDbTag>> tags;
	protected Ptr<List<String>> tagNames = new Ptr<List<String>>();

	public AbstrGkDbItemLastFmWithTags(MusicEntry lastfmMusicEntry, IGkParsingSource source)
	{
		super(lastfmMusicEntry, source);
	}

	@Override
	public List<String> getStyleTagNames() 
	{
		return LazyLoader.loadLazy(this.tagNames, new NameLoader(this.getStyleTags()));
	}

	@Override
	public abstract List<IGkDbTag> getStyleTags();
}

