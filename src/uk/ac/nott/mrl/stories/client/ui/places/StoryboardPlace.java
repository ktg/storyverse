package uk.ac.nott.mrl.stories.client.ui.places;

import uk.ac.nott.mrl.stories.client.StoryversePlace;
import uk.ac.nott.mrl.stories.client.ui.StoryboardView;
import uk.ac.nott.mrl.stories.model.Server;
import uk.ac.nott.mrl.stories.model.Storyboard;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class StoryboardPlace extends StoryversePlace
{
	@Prefix("storyboard")
	public static class Tokenizer implements PlaceTokenizer<StoryboardPlace>
	{
		@Override
		public StoryboardPlace getPlace(final String token)
		{
			return new StoryboardPlace(token);
		}

		@Override
		public String getToken(final StoryboardPlace place)
		{
			return place.id;
		}
	}

	private final String id;

	public StoryboardPlace()
	{
		id = null;
	}

	public StoryboardPlace(final Storyboard story)
	{
		id = story.getId();
	}

	public StoryboardPlace(final String id)
	{
		this.id = id;
	}

	@Override
	public Activity createActivity(final Server server)
	{
		return new StoryboardView(server, id);
	}

	public String getID()
	{
		return id;
	}
}