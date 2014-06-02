package uk.ac.nott.mrl.stories.client.ui.elements;

import org.wornchaos.client.ui.CompositeView;

import uk.ac.nott.mrl.stories.model.Storyboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class StoryboardImageView extends CompositeView<Storyboard>
{
	public static String getImageURL(final String id)
	{
		return GWT.getHostPageBaseURL() + "selectionItem?id=" + id;
	}

	private Image image;

	public StoryboardImageView()
	{
	}

	@Override
	public Widget createView()
	{
		image = new Image();
		return image;
	}

	@Override
	public void valueChanged(final Storyboard value)
	{
		if (value == null || value.getImage() == null) { return; }
		image.setUrl(getImageURL(value.getImage().getId()));
	}
}
