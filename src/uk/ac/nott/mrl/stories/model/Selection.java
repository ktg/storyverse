package uk.ac.nott.mrl.stories.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

@Cache
@Entity
public class Selection
{
	@Id
	private String id;

	@Load
	private List<SelectionItem> items = new ArrayList<SelectionItem>();

	private int voteCount = 10;

	@Load
	@Embed
	@JsonIgnore
	private List<Vote> votes = new ArrayList<Vote>();

	public void add(final SelectionItem item)
	{
		items.add(item);
	}

	public void add(final Vote vote)
	{
		votes.add(vote);
	}

	public void clearVotes()
	{
		votes.clear();
	}

	public int getVoteCount()
	{
		return voteCount;
	}

	public void setVoteCount(final int voteCount)
	{
		this.voteCount = voteCount;
	}

	public String getId()
	{
		return id;
	}

	public Iterable<SelectionItem> getItems()
	{
		return items;
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
