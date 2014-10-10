package com.zwb.geekology.parser.lastfm.util;

import com.zwb.stringutil.StringReformat;

public class StringUtilsLastFm
{
    public static double compare(String string0, String string1)
    {
	String s0 = reformat(string0);
	String s1 = reformat(string1);
	return StringReformat.compareLevenshtein(s0, s1, true);
    }
    
    public static String reformat(String s)
    {
	return s;
    }
    
}
