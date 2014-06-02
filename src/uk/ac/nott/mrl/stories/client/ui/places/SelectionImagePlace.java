package uk.ac.nott.mrl.stories.client.ui.places;

import uk.ac.nott.mrl.stories.client.StoryversePlace;
import uk.ac.nott.mrl.stories.client.ui.SelectionResultView;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class SelectionImagePlace extends StoryversePlace
{
	@Prefix("image")
	public static class Tokenizer implements PlaceTokenizer<SelectionImagePlace>
	{
		@Override
		public SelectionImagePlace getPlace(final String token)
		{
			return new SelectionImagePlace(token);
		}

		@Override
		public String getToken(final SelectionImagePlace place)
		{
			if (place.id == null) { return "new"; }
			return place.id;
		}
	}

	private final String id;

	public SelectionImagePlace()
	{
		id = null;
	}

	public SelectionImagePlace(final String id)
	{
		this.id = id;
	}

	@Override
	public Activity createActivity(final Server server)
	{
		return new SelectionResultView(server, id);
	}

	public String getID()
	{
		return id;
	}
}
