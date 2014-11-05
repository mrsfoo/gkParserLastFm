package com.zwb.geekology.parser.lastfm.util;

import java.util.Collections;
import java.util.List;

import com.sun.xml.internal.ws.util.StringUtils;
import com.zwb.geekology.parser.impl.util.GkParserStringUtils;
import com.zwb.stringutil.ComparisonAlgorithm;
import com.zwb.stringutil.FilterArray;
import com.zwb.stringutil.ISatiniseFilter;
import com.zwb.stringutil.ISatiniseFilterArray;
import com.zwb.stringutil.StringReformat;

public class StringUtilsLastFm
{
    public static ComparisonAlgorithm COMPARISON_ALGORITHM = ComparisonAlgorithm.LEVENSHTEIN;
    
    public static double compareArtists(String name0, String name1)
    {
	String string0 = getAllArtistNameFilters().filter(name0, true);
	String string1 = getAllArtistNameFilters().filter(name1, true);
	return StringReformat.compare(string0, string1, COMPARISON_ALGORITHM);
    }
    
    public static ISatiniseFilterArray getSpecificArtistNameFilters()
    {
	return new FilterArray();
    }
    
    public static ISatiniseFilterArray getSpecificReleaseNameFilters()
    {
	return new FilterArray();
    }
    
    public static ISatiniseFilterArray getSpecificTrackNameFilters()
    {
	return new FilterArray();
    }

    public static ISatiniseFilterArray getSpecificTagNameFilters()
    {
	return new FilterArray();
    }

    public static ISatiniseFilterArray getAllArtistNameFilters()
    {
	return getSpecificArtistNameFilters().add(GkParserStringUtils.getGeneralArtistNameFilters());
    }
    
    public static ISatiniseFilterArray getAllReleaseNameFilters(String artistName)
    {
	return getSpecificReleaseNameFilters().add(GkParserStringUtils.getGeneralReleaseNameFilters(artistName));
    }
    
    public static ISatiniseFilterArray getAllTrackNameFilters()
    {
	return getSpecificTrackNameFilters().add(GkParserStringUtils.getGeneralTrackNameFilters());
    }

    public static ISatiniseFilterArray getAllTagNameFilters()
    {
	return getSpecificTagNameFilters().add(GkParserStringUtils.getGeneralTagNameFilters());
    }
}
