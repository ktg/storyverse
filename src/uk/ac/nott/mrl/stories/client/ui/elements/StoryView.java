package uk.ac.nott.mrl.stories.client.ui.elements;

import org.wornchaos.client.controller.AbstractController;
import org.wornchaos.client.server.Filter;
import org.wornchaos.client.server.Filters;
import org.wornchaos.client.ui.CompositeView;

import uk.ac.nott.mrl.stories.model.Server;
import uk.ac.nott.mrl.stories.model.Story;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class StoryView extends CompositeView<Story>
{
	interface StoryViewUI extends UiBinder<Widget, StoryView>
	{
	}

	private static final StoryViewUI ui = GWT.create(StoryViewUI.class);

	private final AbstractController<Story> controller = new AbstractController<Story>()
	{
		@Override
		protected void save(Story value, AsyncCallback<Story> callback)
		{
			// TODO Auto-generated method stub

		}
	};

	@UiField
	Label title;

	@UiField
	Label text;

	@UiField
	Label author;

	@UiField
	Panel tags;

	private final Filters filters;

	final Server server;

	public StoryView(final Story story, final Filters filters, final Server server)
	{
		this.server = server;
		this.filters = filters;

		controller.add(this);
		controller.setItem(story);
	}

	@Override
	public void valueChanged(final Story value)
	{
		text.setText(value.getText());
		title.setText(value.getTitle());
		author.setText(value.getAuthor().getName());

		tags.clear();

		for (final String tag : value.getTags())
		{
			final Filter filter = new Filter("tags", tag);
			if (filters != null && !filters.contains(filter))
			{
				tags.add(new Button("+ " + tag, new ClickHandler()
				{
					@Override
					public void onClick(final ClickEvent event)
					{
						filters.add(filter);
					}
				}));
			}
			else
			{
				tags.add(new Label(tag));
			}
		}
	}

	@Override
	public Widget createView()
	{
		return ui.createAndBindUi(this);
	}
}