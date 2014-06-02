package uk.ac.nott.mrl.stories.model;

import java.util.Collection;
import java.util.HashSet;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Tags
{
	@Id
	private String id;

	private Collection<String> tags = new HashSet<String>();

	public void add(final String tag)
	{
		tags.add(tag);
	}

	public String getId()
	{
		return id;
	}

	public Collection<String> getTags()
	{
		return tags;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setTags(final Collection<String> tags)
	{
		this.tags.clear();
		this.tags.addAll(tags);
	}
}
