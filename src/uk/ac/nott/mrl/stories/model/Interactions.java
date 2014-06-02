package uk.ac.nott.mrl.stories.model;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Interactions
{
	@Id
	private String id;

	@Load
	@Embed
	private List<Vote> votes = new ArrayList<Vote>();

	public void add(final Vote vote)
	{
		votes.add(vote);
	}

	public String getId()
	{
		return id;
	}

	public List<Vote> getVotes()
	{
		return votes;
	}

	public void setId(final String id)
	{
		this.id = id;
	}
}
