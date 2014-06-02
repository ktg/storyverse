package uk.ac.nott.mrl.stories.client.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.client.ui.Page;
import org.wornchaos.client.ui.ViewCallback;
import org.wornchaos.client.ui.layout.HorizontalLayout;
import org.wornchaos.client.ui.layout.LayoutPanel;
import org.wornchaos.client.ui.layout.VerticalLayout;

import uk.ac.nott.mrl.stories.client.Storyverse;
import uk.ac.nott.mrl.stories.client.ui.elements.SelectionItemView;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionPlace;
import uk.ac.nott.mrl.stories.model.Selection;
import uk.ac.nott.mrl.stories.model.SelectionItem;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class SelectionVoteView extends Page<Selection>
{
	private final HorizontalLayout horizLayout = new HorizontalLayout(3);
	private final VerticalLayout vertLayout = new VerticalLayout(3);

	private final Label status = new Label("Submitting Vote...");

	private final Panel rootPanel = new FlowPanel();

	private final LayoutPanel itemPanel = new LayoutPanel();

	private Audio audio = Audio.createIfSupported();

	private String id;

	private boolean submitting = false;

	private Map<String, SelectionItemView> itemMap = new HashMap<String, SelectionItemView>();

	private HandlerRegistration resizeRegistration;

	private final Server server;

	private final Timer timer = new Timer()
	{
		@Override
		public void run()
		{
			status.getElement().getStyle().setOpacity(0);
		}
	};

	public SelectionVoteView(final Server server, final String id)
	{
		this.server = server;
		this.id = id;
	}

	@Override
	public Widget createView()
	{
		status.setStyleName(Storyverse.getResources().imageCSS().status());
		rootPanel.add(status);
		rootPanel.setStylePrimaryName(Storyverse.getResources().imageCSS().panel());

		setSubmitting(false, null);

		if (audio != null)
		{
			audio.setControls(false);
			audio.setSrc("/tutturuu.mp3");
			audio.load();
			rootPanel.add(audio);
		}

		if (id != null && !id.equals("new"))
		{
			server.getSelection(id, new ViewCallback<Selection>(this));
		}

		resizeRegistration = Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(final ResizeEvent event)
			{
				updateLayout();
			}
		});

		itemPanel.setStyleName(Storyverse.getResources().imageCSS().panel());

		rootPanel.add(itemPanel);

		updateLayout();

		return rootPanel;
	}

	@Override
	public void onStop()
	{
		resizeRegistration.removeHandler();

		rootPanel.getElement().getStyle().clearBackgroundColor();
	}

	private void submitVote(SelectionItem item)
	{
		if (submitting) { return; }
		setSubmitting(true, "Submitting Vote...");
		server.submitVote(id, item.getId(), new AsyncCallback<Selection>()
		{
			@Override
			public void onSuccess(final Selection response)
			{
				valueChanged(response);
				setSubmitting(false, "Thanks!");
				timer.schedule(3000);
			}
		});
		if (audio != null)
		{
			audio.pause();
			audio.setCurrentTime(0);
			audio.play();
		}
	}

	@Override
	public void valueChanged(final Selection selection)
	{
		if (selection == null) { return; }
		if (id != selection.getId())
		{
			id = selection.getId();
			Storyverse.updateURL(new SelectionPlace(id));
		}
		final Collection<String> unusedImages = new HashSet<String>(itemMap.keySet());
		for (final SelectionItem item : selection.getItems())
		{
			if (!itemMap.containsKey(item.getId()))
			{
				final SelectionItemView view = new SelectionItemView();
				view.valueChanged(item);
				view.setStyleName(Storyverse.getResources().imageCSS().image());
				view.addClickHandler(new ClickHandler()
				{
					@Override
					public void onClick(final ClickEvent event)
					{
						submitVote(item);
					}
				});
				itemPanel.add(view);
				itemMap.put(item.getId(), view);
			}
			unusedImages.remove(item.getId());
		}

		for (final String imageID : unusedImages)
		{
			final Widget widget = itemMap.get(imageID);
			itemPanel.remove(widget);
		}

		itemPanel.layout();
	}

	private void setSubmitting(final boolean submitting, final String text)
	{
		this.submitting = submitting;
		if (text == null)
		{
			status.getElement().getStyle().setOpacity(0);
		}
		else
		{
			status.getElement().getStyle().setOpacity(1);
			status.setText(text);
			timer.cancel();
		}
	}

	private void updateLayout()
	{
		if (Window.getClientHeight() < Window.getClientWidth())
		{
			itemPanel.setLayout(horizLayout);
		}
		else
		{
			itemPanel.setLayout(vertLayout);
		}
	}
}