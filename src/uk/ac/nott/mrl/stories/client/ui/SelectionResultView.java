package uk.ac.nott.mrl.stories.client.ui;

import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.client.ui.Page;
import org.wornchaos.client.ui.ViewCallback;
import org.wornchaos.client.ui.layout.HorizontalLayout;
import org.wornchaos.client.ui.layout.LayoutPanel;
import org.wornchaos.logger.Log;

import uk.ac.nott.mrl.stories.client.Storyverse;
import uk.ac.nott.mrl.stories.client.ui.elements.SelectionItemView;
import uk.ac.nott.mrl.stories.model.Selection;
import uk.ac.nott.mrl.stories.model.SelectionItem;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class SelectionResultView extends Page<SelectionItem>
{
	public static String getImageURL(final String id)
	{
		return GWT.getHostPageBaseURL() + "selectionItem?id=" + id;
	}

	private Panel panel;

	private Widget result;

	private String id;

	private Selection selection = null;

	private final Server server;

	private final Timer timer = new Timer()
	{
		@Override
		public void run()
		{
			if (id != null && !id.equals("new"))
			{
				server.getSelectionResult(id, new ViewCallback<SelectionItem>(SelectionResultView.this));
			}
		}
	};

	public SelectionResultView(final Server server, final String id)
	{
		this.server = server;
		this.id = id;
	}

	@Override
	public void onStop()
	{
		RootPanel.get().getWidget(0).getElement().getStyle().clearPosition();
		RootPanel.get().getWidget(0).getElement().getStyle().clearLeft();
		RootPanel.get().getWidget(0).getElement().getStyle().clearTop();
		RootPanel.get().getWidget(0).getElement().getStyle().clearRight();
		RootPanel.get().getWidget(0).getElement().getStyle().clearBottom();
		RootPanel.get().getWidget(0).getElement().getStyle().clearBackgroundColor();

		timer.cancel();
	}

	@Override
	public Widget createView()
	{
		panel = new FlowPanel();
		panel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		panel.getElement().getStyle().setLeft(0, Unit.PX);
		panel.getElement().getStyle().setTop(0, Unit.PX);
		panel.getElement().getStyle().setRight(0, Unit.PX);
		panel.getElement().getStyle().setBottom(0, Unit.PX);
		panel.getElement().getStyle().setBackgroundColor("#000");
		panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		panel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

		RootPanel.get().getWidget(0).getElement().getStyle().setPosition(Position.ABSOLUTE);
		RootPanel.get().getWidget(0).getElement().getStyle().setLeft(0, Unit.PX);
		RootPanel.get().getWidget(0).getElement().getStyle().setTop(0, Unit.PX);
		RootPanel.get().getWidget(0).getElement().getStyle().setRight(0, Unit.PX);
		RootPanel.get().getWidget(0).getElement().getStyle().setBottom(0, Unit.PX);
		RootPanel.get().getWidget(0).getElement().getStyle().setBackgroundColor("#000");

		timer.scheduleRepeating(3000);

		return panel;
	}

	@Override
	public void valueChanged(SelectionItem item)
	{
		if (item == null && result == null)
		{
			if (selection == null)
			{
				server.getSelection(id, new AsyncCallback<Selection>()
				{
					@Override
					public void onSuccess(Selection response)
					{
						selection = response;
						if (result != null) { return; }
						if (response == null) { return; }

						LayoutPanel itemPanel = new LayoutPanel();
						itemPanel.setLayout(new HorizontalLayout(3));

						for (SelectionItem childItem : response.getItems())
						{
							SelectionItemView view = new SelectionItemView();
							view.valueChanged(childItem);
							itemPanel.add(view);
						}

						itemPanel.layout();

						result = itemPanel;

						panel.add(result);
					}
				});
			}
		}

		if (item != null)
		{
			if (result instanceof SelectionItemView)
			{
				SelectionItemView oldResult = (SelectionItemView) result;
				if (item.getId().equals(oldResult.getItem().getId())) { return; }
			}

			if (result != null)
			{
				result.getElement().getStyle().setZIndex(0);
			}

			final Widget oldResult = result;

			final SelectionItemView newResult = new SelectionItemView(false, true);
			newResult.setStyleName(Storyverse.getResources().imageCSS().panel());
			newResult.getElement().getStyle().setZIndex(1);
			newResult.getElement().getStyle().setOpacity(0);
			newResult.setLoadHandler(new LoadHandler()
			{
				@Override
				public void onLoad(LoadEvent event)
				{
					Log.info("Loaded");
					newResult.getElement().getStyle().setProperty("webkitTransition", "opacity 1s ease");
					newResult.getElement().getStyle().setOpacity(1);
					if (oldResult != null)
					{
						Timer timer = new Timer()
						{
							@Override
							public void run()
							{
								panel.remove(oldResult);
							}
						};
						timer.schedule(1000);
					}
				}
			});
			newResult.valueChanged(item);
			newResult.setWidth("100%");

			result = newResult;

			panel.add(result);
		}
	}
}