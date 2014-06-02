package uk.ac.nott.mrl.stories.model;

import java.beans.ConstructorProperties;

import org.wornchaos.client.server.PaginatedList;

public class UserPage<T> extends PaginatedList<T>
{
	private final User user;

	@ConstructorProperties({ "user", "offset", "total", "pageSize" })
	public UserPage(final User user, final int offset, final int total, final int pageSize)
	{
		super(offset, total, pageSize);
		this.user = user;
	}

	public User getUser()
	{
		return user;
	}
}
