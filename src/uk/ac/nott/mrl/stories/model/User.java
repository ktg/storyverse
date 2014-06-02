package uk.ac.nott.mrl.stories.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class User
{
	@Id
	private String id;

	private long lastUpdate = 0;

	@Index
	@JsonIgnore
	private String userId;

	private String name;

	private boolean admin = false;

	// TODO gps?
	// private String location;

	// TODO Length?
	// private String bio;

	public User(final String id)
	{
		this.id = id;
	}

	public User(final String id, final String userID)
	{
		this.id = id;
		userId = userID;
	}

	User()
	{
	}

	public String getId()
	{
		return id;
	}

	public long getLastUpdate()
	{
		return lastUpdate;
	}

	public String getName()
	{
		return name;
	}

	public String getUserId()
	{
		return userId;
	}

	public boolean isAdmin()
	{
		return admin;
	}

	public void setLastUpdate(final long update)
	{
		lastUpdate = update;
	}
}