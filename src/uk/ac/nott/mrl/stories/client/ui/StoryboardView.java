package uk.ac.nott.mrl.stories.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.wornchaos.client.controller.AbstractReadOnlyController;
import org.wornchaos.client.controller.DelegateController;
import org.wornchaos.client.controller.AbstractController;
import org.wornchaos.client.ui.Page;
import org.wornchaos.client.ui.View;
import org.wornchaos.client.ui.layout.LayoutPanel;
import org.wornchaos.client.ui.layout.SlideLayout;
import org.wornchaos.logger.Log;

import uk.ac.nott.mrl.stories.client.Storyverse;
import uk.ac.nott.mrl.stories.client.ui.elements.StoryEditView;
import uk.ac.nott.mrl.stories.client.ui.elements.StoryView;
import uk.ac.nott.mrl.stories.client.ui.elements.StoryboardImageView;
import uk.ac.nott.mrl.stories.client.ui.elements.StoryboardQAView;
import uk.ac.nott.mrl.stories.client.ui.places.StoryboardPlace;
import uk.ac.nott.mrl.stories.model.Server;
import uk.ac.nott.mrl.stories.model.Story;
import uk.ac.nott.mrl.stories.model.Storyboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class StoryboardView extends Page<Storyboard>
{
	interface StorySetEditorUiBinder extends UiBinder<Widget, StoryboardView>
	{
	}

	interface Style extends CssResource
	{
		String story();
	}

	private static StorySetEditorUiBinder uiBinder = GWT.create(StorySetEditorUiBinder.class);

	@UiField
	Style style;

	@UiField
	LayoutPanel stories;

	@UiField
	Button prev;

	@UiField
	Button next;

	@UiField
	StoryboardImageView image;

	@UiField
	StoryboardQAView firstQ;

	@UiField
	StoryboardQAView secondQ;

	@UiField
	StoryEditView storyEdit;

	private final Server server;

	private final AbstractController<Storyboard> controller = new AbstractController<Storyboard>()
	{
		@Override
		protected void save(final Storyboard value, final AsyncCallback<Storyboard> callback)
		{
			server.setStoryboard(value, callback);
		}
	};

	private final AbstractReadOnlyController<Story> storyController = new DelegateController<Story>(controller)
	{
		@Override
		public Story getValue()
		{
			if (controller.getValue() == null) { return null; }
			return controller.getValue().getStory();
		}

		@Override
		public void setValue(final Story value)
		{
			if (controller.getValue() == null) { return; }
			controller.getValue().setStory(value);
		}
	};

	private Map<String, View<Story>> storyViews = new HashMap<String, View<Story>>();

	public StoryboardView(final Server server, final String id)
	{
		this.server = server;

		controller.add(this);

		server.getStoryboard(id, controller);
	}

	@Override
	public Widget createView()
	{
		final Widget widget = uiBinder.createAndBindUi(this);

		stories.setLayout(new SlideLayout(80, 5, style.story()));

		controller.add(firstQ);
		controller.add(secondQ);
		controller.add(image);

		updateButtons();

		return widget;
	}

	@Override
	public void valueChanged(final Storyboard value)
	{
		Log.info("Value changed");
		if (value == null) { return; }
		if (value.getId() != null && !Window.Location.getHash().contains(value.getId()))
		{
			Storyverse.updateURL(new StoryboardPlace(value));
		}

		int index = 0;
		for (final Story story : value.getStories())
		{
			if (storyViews.containsKey(story.getId()))
			{
				final View<Story> view = storyViews.get(story.getId());
				view.valueChanged(story);
			}
			else
			{
				Log.info("Adding Story " + story.getId());
				final StoryView view = new StoryView(story, null, server);
				storyViews.put(story.getId(), view);
				stories.insert(view, index);
				index++;
			}
		}

		storyEdit.valueChanged(value.getStory());

		updateButtons();
	}

	@UiFactory
	StoryboardQAView createQAView()
	{
		return new StoryboardQAView(controller);
	}

	@UiFactory
	StoryEditView createStoryEditor()
	{
		return new StoryEditView(storyController);
	}

	@UiHandler("next")
	void next(final ClickEvent event)
	{
		stories.selectNext();
		updateButtons();
	}

	@UiHandler("prev")
	void previous(final ClickEvent event)
	{
		stories.selectPrevious();
		updateButtons();
	}

	private void updateButtons()
	{
		if (stories.getSelected() == 0)
		{
			prev.setVisible(false);
		}
		else
		{
			prev.setVisible(true);
		}

		if (stories.getSelected() >= stories.getWidgetCount() - 1)
		{
			next.setVisible(false);
		}
		else
		{
			next.setVisible(true);
		}
	}
}