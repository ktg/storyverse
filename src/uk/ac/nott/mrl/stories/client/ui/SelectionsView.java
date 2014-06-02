package uk.ac.nott.mrl.stories.client.ui;

import org.wornchaos.client.ui.Page;
import org.wornchaos.client.ui.ViewCallback;

import uk.ac.nott.mrl.stories.client.Storyverse;
import uk.ac.nott.mrl.stories.client.ui.places.BoredPlace;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionImagePlace;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionPlace;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionUploadPlace;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

//@Place("selections")
public class SelectionsView extends Page<Iterable<String>>
{
	private final Server server;

	private final Panel selectionsPanel = new FlowPanel();

	public SelectionsView(final Server server)
	{
		this.server = server;
	}

	@Override
	public Widget createView()
	{
		server.getSelections(new ViewCallback<Iterable<String>>(this));
		return selectionsPanel;
	}

	@Override
	public void valueChanged(Iterable<String> selections)
	{
		selectionsPanel.clear();
		selectionsPanel.getElement().getStyle().setFontSize(20, Unit.PX);
		selectionsPanel.add(new Hyperlink("Opinion", Storyverse.getToken(new BoredPlace())));
		selectionsPanel.add(new Hyperlink("Create New Selection", Storyverse.getToken(new SelectionUploadPlace())));
		if (selections == null) { return; }
		for (final String selectionID : selections)
		{
			final Panel panel = new FlowPanel();

			panel.add(new InlineHyperlink("Vote on " + selectionID, Storyverse
					.getToken(new SelectionPlace(selectionID))));

			panel.add(new InlineLabel(" | "));
			panel.add(new InlineHyperlink("Selected Image for " + selectionID, Storyverse
					.getToken(new SelectionImagePlace(selectionID))));

			panel.add(new InlineLabel(" | "));
			panel.add(new InlineHyperlink("Edit " + selectionID, Storyverse.getToken(new SelectionUploadPlace(
					selectionID))));
			selectionsPanel.add(panel);
		}
	}
}