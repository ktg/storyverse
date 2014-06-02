package uk.ac.nott.mrl.stories.client.ui.places;

import uk.ac.nott.mrl.stories.client.StoryversePlace;
import uk.ac.nott.mrl.stories.client.ui.StoryEditor;
import uk.ac.nott.mrl.stories.model.Server;
import uk.ac.nott.mrl.stories.model.Story;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class StoryPlace extends StoryversePlace
{
	@Prefix("story")
	public static class Tokenizer implements PlaceTokenizer<StoryPlace>
	{
		@Override
		public StoryPlace getPlace(final String token)
		{
			return new StoryPlace(token);
		}

		@Override
		public String getToken(final StoryPlace place)
		{
			if (place.id == null) { return "new"; }
			return place.id;
		}
	}

	private final String id;

	public StoryPlace()
	{
		id = null;
	}

	public StoryPlace(final Story story)
	{
		id = story.getId();
	}

	public StoryPlace(final String id)
	{
		this.id = id;
	}

	@Override
	public Activity createActivity(final Server server)
	{
		return new StoryEditor(server, id);
	}

	public String getID()
	{
		return id;
	}
}
