package uk.ac.nott.mrl.stories.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.wornchaos.client.server.Modified;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Cache
@Entity
public class Storyboard
{
	@Id
	private String id;

	private Privacy privacy;

	@Index
	private User author;

	private Date modified;

	@Load
	private List<Story> stories = new ArrayList<Story>();

	private SelectionItem image;

	private String tag;

	private String answer1;

	private String answer2;

	@Load
	private Story story;

	// private String connection;

	public Storyboard()
	{

	}

	public void add(final Story story)
	{
		stories.add(story);
	}

	public String getAnswer1()
	{
		return answer1;
	}

	public String getAnswer2()
	{
		return answer2;
	}

	public User getAuthor()
	{
		return author;
	}

	public String getId()
	{
		return id;
	}

	public SelectionItem getImage()
	{
		return image;
	}

	@Modified
	public Date getModified()
	{
		return modified;
	}

	public String getTag()
	{
		return tag;
	}

	public Privacy getPrivacy()
	{
		return privacy;
	}

	public Iterable<Story> getStories()
	{
		return stories;
	}

	public Story getStory()
	{
		return story;
	}

	public void setAnswer1(final String answer1)
	{
		this.answer1 = answer1;
	}

	public void setAnswer2(final String answer2)
	{
		this.answer2 = answer2;
	}

	public void setAuthor(final User author)
	{
		this.author = author;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setImage(final SelectionItem image)
	{
		this.image = image;
	}

	public void setModified(final Date modified)
	{
		this.modified = modified;
	}

	public void setPrivacy(final Privacy privacy)
	{
		this.privacy = privacy;
	}

	public void setStory(final Story story)
	{
		this.story = story;
	}
}