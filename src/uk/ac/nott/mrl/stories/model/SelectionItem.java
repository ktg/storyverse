package uk.ac.nott.mrl.stories.model;

import java.util.Collection;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class SelectionItem
{
	@Id
	private String id;

	@JsonIgnore
	private String blobID;

	@JsonIgnore
	private String smallBlobID;

	private String contentType;

	private Collection<String> tags = new HashSet<String>();

	SelectionItem()
	{

	}

	public SelectionItem(String blobID, String smallBlobID)
	{
		this.blobID = blobID;
		this.smallBlobID = smallBlobID;
	}

	public void add(String tag)
	{
		tags.add(tag);
	}

	public String getId()
	{
		return id;
	}

	public String getBlobID()
	{
		return blobID;
	}

	public String getSmallBlobID()
	{
		return smallBlobID;
	}

	public Iterable<String> getTags()
	{
		return tags;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
}
