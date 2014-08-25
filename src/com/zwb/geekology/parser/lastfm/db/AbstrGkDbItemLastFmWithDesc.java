package com.zwb.geekology.parser.lastfm.db;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbItemWithDesc;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;

import de.umass.lastfm.MusicEntry;

public class AbstrGkDbItemLastFmWithDesc extends AbstrGkDbItemLastFm implements IGkDbItemWithDesc
{
	private String summary;
	private String description;

	public AbstrGkDbItemLastFmWithDesc(MusicEntry lastfmMusicEntry, IGkParsingSource source)
	{
		super(lastfmMusicEntry, source);
	}

	@Override
	public String getDescriptionSummary()
	{
		if(this.summary==null)
		{
			this.summary = this.lastfmMusicEntry.getWikiSummary();
		}
		return this.summary;
	}

	@Override
	public String getDescription() 
	{
		if(this.description==null)
		{
			this.description = this.lastfmMusicEntry.getWikiText();
		}
		return this.description;
	}
}
