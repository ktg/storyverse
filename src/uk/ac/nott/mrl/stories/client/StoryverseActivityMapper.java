package uk.ac.nott.mrl.stories.client;

import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class StoryverseActivityMapper implements ActivityMapper
{
	private final Server server;

	public StoryverseActivityMapper(final Server server)
	{
		this.server = server;
	}

	@Override
	public Activity getActivity(final Place place)
	{
		if (place instanceof StoryversePlace) { return ((StoryversePlace) place).createActivity(server); }
		return null;
	}

}
