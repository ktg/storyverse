package uk.ac.nott.mrl.stories.client.ui.places;

import uk.ac.nott.mrl.stories.client.StoryversePlace;
import uk.ac.nott.mrl.stories.client.ui.BoredView;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class BoredPlace extends StoryversePlace
{
	@Prefix("bored")
	public static class Tokenizer implements PlaceTokenizer<BoredPlace>
	{
		@Override
		public BoredPlace getPlace(final String token)
		{
			return new BoredPlace();
		}

		@Override
		public String getToken(final BoredPlace place)
		{
			return "";
		}
	}

	public BoredPlace()
	{
	}

	@Override
	public Activity createActivity(final Server server)
	{
		return new BoredView(server);
	}
}
