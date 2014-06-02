package uk.ac.nott.mrl.stories.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.wornchaos.client.server.Modified;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class Story
{
	@Id
	private String id;

	@Index
	private User author;

	private String text;

	private String title;

	private Date modified;

	@Index
	private Privacy privacy = Privacy.Private;

	@Index
	private Collection<String> tags = new HashSet<String>();

	public Story()
	{

	}

	public void add(final String tag)
	{
		tags.add(tag);
	}

	public User getAuthor()
	{
		return author;
	}

	public String getId()
	{
		return id;
	}

	@Modified
	public Date getModified()
	{
		return modified;
	}

	public Iterable<String> getTags()
	{
		return tags;
	}

	public String getText()
	{
		return text;
	}

	public String getTitle()
	{
		return title;
	}

	public void remove(final String tag)
	{
		tags.remove(tag);
	}

	public void setAuthor(final User author)
	{
		this.author = author;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setModified(final Date modified)
	{
		this.modified = modified;
	}

	public void setText(final String text)
	{
		this.text = text;
	}

	public void setTitle(final String title)
	{
		this.title = title;
	}

	public Privacy getPrivacy()
	{
		return privacy;
	}

	public void setPrivacy(Privacy privacy)
	{
		this.privacy = privacy;
	}
}