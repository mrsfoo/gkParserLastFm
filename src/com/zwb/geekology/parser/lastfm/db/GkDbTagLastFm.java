package com.zwb.geekology.parser.lastfm.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.enums.GkParsingEventType;
import com.zwb.geekology.parser.impl.util.GkParserStringUtils;
import com.zwb.geekology.parser.impl.util.NameLoader;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.geekology.parser.lastfm.util.StringUtilsLastFm;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;
import com.zwb.stringutil.ISatiniseFilterArray;

import de.umass.lastfm.CallException;
import de.umass.lastfm.Tag;

public class GkDbTagLastFm extends AbstrGkDbItem implements IGkDbTag
{
    private Tag tag;
    private Ptr<String> summary = new Ptr<>();
    private Ptr<String> description = new Ptr<>();
    private Ptr<List<IGkDbTag>> similar = new Ptr<>();
    private Ptr<List<String>> similarNames = new Ptr<>();
    private Ptr<Double> weight = new Ptr<>();
    
    public GkDbTagLastFm(Tag tag)
    {
	super(tag.getName(), GkParserObjectFactory.createSource(Config.getSourceString()));
	this.tag = tag;
    }
    
    @Override
    public Double getWeight()
    {
	try
	{
	    return LazyLoader.loadLazy(this.weight, new WeightLoader());
	}
	catch (CallException e)
	{
	    this.addEvent(GkParserObjectFactory.createParsingEvent(GkParsingEventType.EXTERNAL_ERROR, "exception in last.fm framework while loading weight of tag <" + this.getName() + ">; probably bad internet connection: " + e.getClass().getName() + " -- " + e.getMessage(), this.getSource()));
	    return null;
	}
    }
    
    @Override
    public List<IGkDbTag> getSimilar()
    {
	try
	{
	    return LazyLoader.loadLazy(this.similar, new SimilarLoader());
	}
	catch (CallException e)
	{
	    this.addEvent(GkParserObjectFactory.createParsingEvent(GkParsingEventType.EXTERNAL_ERROR, "exception in last.fm framework while loading similar tags of tag <" + this.getName() + ">; probably bad internet connection: " + e.getClass().getName() + " -- " + e.getMessage(), this.getSource()));
	    return null;
	}
    }
    
    @Override
    public List<String> getSimilarsNames()
    {
	return LazyLoader.loadLazy(this.similarNames, new NameLoader(this.getSimilar()));
    }
    
    @Override
    public String getDescriptionSummary()
    {
	try
	{
	    return LazyLoader.loadLazy(this.summary, new SummaryLoader());
	}
	catch (CallException e)
	{
	    this.addEvent(GkParserObjectFactory.createParsingEvent(GkParsingEventType.EXTERNAL_ERROR, "exception in last.fm framework while loading description summary of tag <" + this.getName() + ">; probably bad internet connection: " + e.getClass().getName() + " -- " + e.getMessage(), this.getSource()));
	    return null;
	}
    }
    
    @Override
    public String getDescription()
    {
	try
	{
	    return LazyLoader.loadLazy(this.description, new DescLoader());
	}
	catch (CallException e)
	{
	    this.addEvent(GkParserObjectFactory.createParsingEvent(GkParsingEventType.EXTERNAL_ERROR, "exception in last.fm framework while loading description of tag <" + this.getName() + ">; probably bad internet connection: " + e.getClass().getName() + " -- " + e.getMessage(), this.getSource()));
	    return null;
	}
    }
    

    public boolean hasDescriptionSummary()
    {
	String s = getDescriptionSummary();
	if((s==null)||s.isEmpty())
	{
	    return false;
	}
	return true;
    }
    
    public boolean hasDescription()
    {
	String s = getDescription();
	if((s==null)||s.isEmpty())
	{
	    return false;
	}
	return true;	
    }

    @Override
    public boolean hasSimilar()
    {
	List<IGkDbTag> l = getSimilar();
	if((l==null)||l.isEmpty())
	{
	    return false;
	}
	return true;	
    }
    
    class SummaryLoader implements ILoader<String>
    {
	@Override
	public String load()
	{
	    return GkDbTagLastFm.this.tag.getWikiSummary();
	}
    }
    
    class DescLoader implements ILoader<String>
    {
	@Override
	public String load()
	{
	    return GkDbTagLastFm.this.tag.getWikiText();
	}
    }
    
    class WeightLoader implements ILoader<Double>
    {
	@Override
	public Double load()
	{
	    return new Double(GkDbTagLastFm.this.tag.getCount());
	}
    }
    
    class SimilarLoader implements ILoader
    {
	@Override
	public List<IGkDbTag> load()
	{
	    Collection<Tag> sim = Tag.getSimilar(GkDbTagLastFm.this.tag.getName(), Config.getApiKey());
	    List<IGkDbTag> similar = new ArrayList<>();
	    for (Tag t : sim)
	    {
		similar.add(new GkDbTagLastFm(t));
	    }
	    return similar;
	}
    }

    @Override
    public int compareTo(IGkDbTag o)
    {
	if(this.getWeight()>o.getWeight())
	{
	    return -1;
	}
	else if(this.getWeight()<o.getWeight())
	{
	    return 1;
	}
	else
	{
	    return this.getName().compareTo(o.getName());
	}
    }

    @Override
    public ISatiniseFilterArray getFilters()
    {
	return StringUtilsLastFm.getAllTagNameFilters();
    }
}
