package com.zwb.geekology.parser.lastfm.junit;

import junit.framework.TestCase;

import com.zwb.geekology.parser.api.parser.GkParserQueryFactory;
import com.zwb.geekology.parser.lastfm.GkParserLastFm;

public class ArtistQueryTest extends TestCase
{
	public void testQuery()
	{
		GkParserLastFm parser = new GkParserLastFm();
		parser.parse(GkParserQueryFactory.createQueryForArtist("scott walker"));		
	}
	
	
}
