package uk.ac.nott.mrl.stories.client.ui.places;

import uk.ac.nott.mrl.stories.client.StoryversePlace;
import uk.ac.nott.mrl.stories.client.ui.SelectionsView;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class SelectionsPlace extends StoryversePlace
{
	@Prefix("selections")
	public static class Tokenizer implements PlaceTokenizer<SelectionsPlace>
	{
		@Override
		public SelectionsPlace getPlace(final String token)
		{
			return new SelectionsPlace();
		}

		@Override
		public String getToken(final SelectionsPlace place)
		{
			return "";
		}
	}

	public SelectionsPlace()
	{
	}

	@Override
	public Activity createActivity(final Server server)
	{
		return new SelectionsView(server);
	}
}
