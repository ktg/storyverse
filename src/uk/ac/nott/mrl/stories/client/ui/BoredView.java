package uk.ac.nott.mrl.stories.client.ui;

import org.wornchaos.client.ui.Page;
import org.wornchaos.client.ui.ViewCallback;
import org.wornchaos.client.ui.layout.HorizontalLayout;
import org.wornchaos.client.ui.layout.LayoutPanel;
import org.wornchaos.client.ui.layout.VerticalLayout;
import org.wornchaos.logger.Log;

import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BoredView extends Page<String>
{
	interface ImageUploadViewUiBinder extends UiBinder<Widget, BoredView>
	{
	}

	private HorizontalLayout horizLayout;
	private VerticalLayout vertLayout;

	private static ImageUploadViewUiBinder uiBinder = GWT.create(ImageUploadViewUiBinder.class);

	@UiField
	Label status;

	@UiField
	LayoutPanel layout;

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

	public BoredView(final Server server)
	{
		this.server = server;
	}

	@UiHandler({ "bored", "excited" })
	void click(final ClickEvent e)
	{
		String tag = "bored";
		if (e.getSource() instanceof Button)
		{
			Button source = (Button) e.getSource();
			if (source.getTitle() != null)
			{
				tag = source.getTitle();
			}
		}
		server.addInteraction(tag, new ViewCallback<String>(this));
	}

	@Override
	public void onStop()
	{
		resizeRegistration.removeHandler();
	}

	@Override
	public Widget createView()
	{
		final Widget widget = uiBinder.createAndBindUi(this);

		int widgets = layout.getWidgetCount();

		horizLayout = new HorizontalLayout(widgets);
		vertLayout = new VerticalLayout(widgets);

		resizeRegistration = Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(final ResizeEvent event)
			{
				updateLayout();
			}
		});

		updateLayout();

		return widget;
	}

	private void updateLayout()
	{
		if (Window.getClientHeight() < Window.getClientWidth())
		{
			layout.setLayout(horizLayout);
		}
		else
		{
			layout.setLayout(vertLayout);
		}
	}

	@Override
	public void valueChanged(String response)
	{
		status.getElement().getStyle().setOpacity(1);
		Log.info(response);
		status.setText(response);
		timer.cancel();
		timer.schedule(5000);
	}
}