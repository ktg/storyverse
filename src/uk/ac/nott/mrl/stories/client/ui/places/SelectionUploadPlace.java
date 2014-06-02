package uk.ac.nott.mrl.stories.client.ui.places;

import uk.ac.nott.mrl.stories.client.StoryversePlace;
import uk.ac.nott.mrl.stories.client.ui.SelectionUploadView;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class SelectionUploadPlace extends StoryversePlace
{
	@Prefix("upload")
	public static class Tokenizer implements PlaceTokenizer<SelectionUploadPlace>
	{
		@Override
		public SelectionUploadPlace getPlace(final String token)
		{
			return new SelectionUploadPlace(token);
		}

		@Override
		public String getToken(final SelectionUploadPlace place)
		{
			if (place.id == null) { return "new"; }
			return place.id;
		}
	}

	private final String id;

	public SelectionUploadPlace()
	{
		id = null;
	}

	public SelectionUploadPlace(final String id)
	{
		this.id = id;
	}

	@Override
	public Activity createActivity(final Server server)
	{
		return new SelectionUploadView(server, id);
	}

	public String getID()
	{
		return id;
	}
}
