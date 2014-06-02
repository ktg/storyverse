package uk.ac.nott.mrl.stories.client;

import uk.ac.nott.mrl.stories.client.ui.elements.StoryverseHeader;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionsPlace;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Storyverse implements EntryPoint
{
	private static final Resource INSTANCE = GWT.create(Resource.class);

	private static final EventBus eventBus = new SimpleEventBus();
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final StoryverseHistoryMapper historyMapper = GWT.create(StoryverseHistoryMapper.class);

	public static Resource getResources()
	{
		INSTANCE.imageCSS().ensureInjected();

		return INSTANCE;
	}

	public static String getToken(final Place place)
	{
		return historyMapper.getToken(place);
	}

	public static void goTo(final Place place)
	{
		placeController.goTo(place);
	}

	public static void updateURL(final Place place)
	{
		History.newItem(getToken(place), false);
	}

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad()
	{
		final Server server = GWT.create(Server.class);

		final FlowPanel flowPanel = new FlowPanel();
		final SimplePanel panel = new SimplePanel();

		flowPanel.add(new StoryverseHeader(server));
		flowPanel.add(panel);

		// Start ActivityManager for the main widget with our ActivityMapper
		final ActivityMapper activityMapper = new StoryverseActivityMapper(server);
		final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(panel);

		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

		final Place defaultPlace = new SelectionsPlace();

		historyHandler.register(placeController, eventBus, defaultPlace);

		RootPanel.get().add(flowPanel);
		// Goes to the place represented on URL else default place
		historyHandler.handleCurrentHistory();
	}
}
