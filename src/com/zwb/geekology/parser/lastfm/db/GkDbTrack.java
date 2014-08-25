package com.zwb.geekology.parser.lastfm.db;

import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.db.IGkDbTrack;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.lastfm.Config;

import de.umass.lastfm.Track;

public class GkDbTrack extends AbstrGkDbItemLastFmWithDesc implements IGkDbTrack
{
	Track track;
	IGkDbRelease release;
	IGkDbArtist artist;
	
	public GkDbTrack(Track track, IGkDbArtist artist, IGkDbRelease release)
	{
		super(track, GkParserObjectFactory.createSource(Config.getSourceString()));
		this.track = track;
		this.artist = artist;
		this.release = release;
		
		this.track.getDuration();
		this.track.getLastFmInfo(Config.getApiKey());
		this.track.getLocation();
		this.track.getTags();
		this.track.getWikiText();
		this.track.getWikiSummary();
	}
	
	@Override
	public int getTrackNo() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IGkDbRelease getRelease() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGkDbRelease getArtist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<IGkDbTag> getStyleTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getStyleTagNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
