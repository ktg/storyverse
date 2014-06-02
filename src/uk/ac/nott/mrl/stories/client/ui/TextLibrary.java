package uk.ac.nott.mrl.stories.client.ui;

import org.wornchaos.client.server.Filters;
import org.wornchaos.client.server.PaginatedList;
import org.wornchaos.client.ui.Page;
import org.wornchaos.client.ui.ViewCallback;
import org.wornchaos.client.ui.layout.LayoutPanel;
import org.wornchaos.client.ui.layout.SlideLayout;

import uk.ac.nott.mrl.stories.client.ui.elements.StoryView;
import uk.ac.nott.mrl.stories.model.Server;
import uk.ac.nott.mrl.stories.model.Story;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class TextLibrary extends Page<PaginatedList<Story>>
{
	interface StoryListUiBinder extends UiBinder<Widget, TextLibrary>
	{
	}

	interface Style extends CssResource
	{
		String story();
	}

	private static StoryListUiBinder uiBinder = GWT.create(StoryListUiBinder.class);

	@UiField
	Style style;

	@UiField
	LayoutPanel stories;

	private final Filters filters = new Filters();

	private final Server server;

	public TextLibrary(final Server server, final String filters)
	{
		this.server = server;
	}

	@UiHandler("next")
	void next(final ClickEvent event)
	{
		stories.selectNext();
	}

	@UiHandler("prev")
	void previous(final ClickEvent event)
	{
		stories.selectPrevious();
	}

	@Override
	public Widget createView()
	{
		final Widget widget = uiBinder.createAndBindUi(this);

		stories.setLayout(new SlideLayout(80, 5, style.story()));

		server.listStories(filters.getFilters(), 0, new ViewCallback<PaginatedList<Story>>(this));

		return widget;
	}

	@Override
	public void valueChanged(PaginatedList<Story> value)
	{
		for (final Story story : value.getItems())
		{
			stories.add(new StoryView(story, filters, server));
		}
	}
}