package com.zwb.geekology.parser.lastfm.db;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;

import de.umass.lastfm.MusicEntry;

public class AbstrGkDbItemLastFm extends AbstrGkDbItem
{
	protected MusicEntry lastfmMusicEntry;

	public AbstrGkDbItemLastFm(MusicEntry lastfmMusicEntry, IGkParsingSource source)
	{
		super(lastfmMusicEntry.getName(), source);
	}

}
