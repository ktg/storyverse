package uk.ac.nott.mrl.stories.client.ui.places;

import uk.ac.nott.mrl.stories.client.StoryversePlace;
import uk.ac.nott.mrl.stories.client.ui.TextLibrary;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class StoryListPlace extends StoryversePlace
{
	@Prefix("stories")
	public static class Tokenizer implements PlaceTokenizer<StoryListPlace>
	{
		@Override
		public StoryListPlace getPlace(final String token)
		{
			return new StoryListPlace(token);
		}

		@Override
		public String getToken(final StoryListPlace place)
		{
			return place.filters;
		}
	}

	private final String filters;

	public StoryListPlace()
	{
		filters = null;
	}

	public StoryListPlace(final String filters)
	{
		this.filters = filters;
	}

	@Override
	public Activity createActivity(final Server server)
	{
		return new TextLibrary(server, filters);
	}

	public String getFilters()
	{
		return filters;
	}
}