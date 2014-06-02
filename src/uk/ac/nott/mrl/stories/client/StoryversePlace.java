package uk.ac.nott.mrl.stories.client;

import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;

public abstract class StoryversePlace extends Place
{
	public abstract Activity createActivity(final Server server);
}
