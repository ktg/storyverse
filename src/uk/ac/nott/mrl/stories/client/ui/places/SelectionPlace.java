package uk.ac.nott.mrl.stories.client.ui.places;

import uk.ac.nott.mrl.stories.client.StoryversePlace;
import uk.ac.nott.mrl.stories.client.ui.SelectionVoteView;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class SelectionPlace extends StoryversePlace
{
	@Prefix("select")
	public static class Tokenizer implements PlaceTokenizer<SelectionPlace>
	{
		@Override
		public SelectionPlace getPlace(final String token)
		{
			return new SelectionPlace(token);
		}

		@Override
		public String getToken(final SelectionPlace place)
		{
			if (place.id == null) { return "new"; }
			return place.id;
		}
	}

	private final String id;

	public SelectionPlace()
	{
		id = null;
	}

	public SelectionPlace(final String id)
	{
		this.id = id;
	}

	@Override
	public Activity createActivity(final Server server)
	{
		return new SelectionVoteView(server, id);
	}

	public String getID()
	{
		return id;
	}
}
