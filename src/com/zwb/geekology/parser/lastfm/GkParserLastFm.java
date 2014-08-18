package com.zwb.geekology.parser.lastfm;

import com.zwb.config.api.ConfigurationFactory;
import com.zwb.config.api.IConfiguration;
import com.zwb.config.api.NoConfigurationException;
import com.zwb.geekology.parser.api.parser.IGkParser;
import com.zwb.geekology.parser.api.parser.IGkParserQuery;
import com.zwb.geekology.parser.api.parser.IGkParserResult;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.geekology.parser.lastfm.util.MyLogger;

import de.umass.lastfm.Caller;

public class GkParserLastFm implements IGkParser
{
	private MyLogger log = new MyLogger(this.getClass());
	private IConfiguration config = ConfigurationFactory.getConfiguration("lastfm.config");
	
	public GkParserLastFm()
	{
		Caller.getInstance().setUserAgent(config.getString("access.user-agent", "tst"));
		
	}
	
	@Override
	public IGkParserResult parse(IGkParserQuery query)
	{
		if(query.isSampler())
		{
//			query for sampler
			log.debug("query for sampler "+query.getRelease());
		}
		else if (query.hasRelease())
		{
//			query with artist+album
			log.debug("query for artist "+query.getArtist()+"with release "+query.getRelease());
		}
		else
		{
//			query with artist only
			log.debug("query for artist "+query.getArtist());
			
		}
		return null;
	}

	@Override
	public IGkParsingSource getSource() {
		// TODO Auto-generated method stub
		return null;
	}

}
