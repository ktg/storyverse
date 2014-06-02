package uk.ac.nott.mrl.stories.model;

import java.beans.ConstructorProperties;
import java.util.Date;

public class Vote
{
	private String ipAddress;
	private String item;
	private Date timestamp;

	@ConstructorProperties({ "ipAddress", "item" })
	public Vote(final String ipAddress, final String item)
	{
		this.ipAddress = ipAddress;
		this.item = item;
		this.timestamp = new Date();
	}

	Vote()
	{

	}

	public String getItem()
	{
		return item;
	}

	public String getIpAddress()
	{
		return ipAddress;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}
}