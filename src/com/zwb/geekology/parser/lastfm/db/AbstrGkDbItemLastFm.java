package com.zwb.geekology.parser.lastfm.db;

import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.api.parser.IGkParsingEvent;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.geekology.parser.enums.GkParsingEventType;
import com.zwb.geekology.parser.lastfm.Config;

import de.umass.lastfm.CallException;
import de.umass.lastfm.MusicEntry;

public abstract class AbstrGkDbItemLastFm extends AbstrGkDbItem
{
	protected MusicEntry lastfmMusicEntry;

	public AbstrGkDbItemLastFm(MusicEntry lastfmMusicEntry, IGkParsingSource source)
	{
		super(lastfmMusicEntry.getName(), source);
		this.lastfmMusicEntry = lastfmMusicEntry;
	}
	
}
