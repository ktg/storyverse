package uk.ac.nott.mrl.stories.client.ui.elements;

import org.wornchaos.client.ui.View;

import uk.ac.nott.mrl.stories.model.SelectionItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class SelectionItemView extends Composite implements View<SelectionItem>
{
	public static String getItemURL(final String id, final boolean thumb)
	{
		if (thumb) { return GWT.getHostPageBaseURL() + "selectionItem?id=" + id + "&thumb=" + thumb; }
		return GWT.getHostPageBaseURL() + "selectionItem?id=" + id;
	}

	private SelectionItem item;

	private Panel panel = new FlowPanel();

	private Widget itemWidget;

	private boolean playAudio = false;

	private boolean thumb = true;

	private LoadHandler loadHandler = null;

	public SelectionItemView()
	{
		initWidget(panel);
	}

	public SelectionItemView(final boolean thumb, final boolean playAudio)
	{
		initWidget(panel);
		this.thumb = thumb;
		this.playAudio = playAudio;
	}

	@Override
	public void valueChanged(SelectionItem value)
	{
		if (item != null)
		{
			if (item.getId().equals(value.getId()))
			{
				return;
			}
			else if (itemWidget != null)
			{
				panel.remove(itemWidget);
			}
		}

		item = value;

		if (item.getContentType() == null || item.getContentType().startsWith("image/"))
		{
			itemWidget = new Image(getItemURL(item.getId(), thumb));
			if (loadHandler != null)
			{
				((Image) itemWidget).addLoadHandler(loadHandler);
			}
			itemWidget.getElement().getStyle().setProperty("maxWidth", "100%");
			itemWidget.getElement().getStyle().setProperty("maxHeight", "100%");
			panel.add(itemWidget);
		}
		else if (item.getContentType().startsWith("audio/"))
		{
			if (playAudio)
			{
				Audio audio = Audio.createIfSupported();
				audio.setAutoplay(true);
				if (audio != null)
				{
					audio.setSrc(getItemURL(item.getId(), thumb));
				}
				itemWidget = audio;
				itemWidget.setWidth("100%");
				itemWidget.setHeight("100%");
				panel.add(itemWidget);
			}
			else
			{
				itemWidget = new FlowPanel();
				String color = Integer.toHexString((MurmurHash.hash32(item.getId()) & 0xffffff) | 0x1000000)
						.substring(1);
				itemWidget.getElement().getStyle().setBackgroundColor("#" + color);
				itemWidget.setWidth("100%");
				itemWidget.setHeight("100%");
				panel.add(itemWidget);
			}
		}
	}

	@Override
	public Widget createView()
	{
		return panel;
	}

	@Override
	public void focus()
	{
	}

	public void addClickHandler(ClickHandler clickHandler)
	{
		addDomHandler(clickHandler, ClickEvent.getType());
	}

	public void addTouchEndHandler(TouchEndHandler touchHandler)
	{
		addDomHandler(touchHandler, TouchEndEvent.getType());
	}

	public SelectionItem getItem()
	{
		return item;
	}

	public void setLoadHandler(LoadHandler loadHandler)
	{
		this.loadHandler = loadHandler;

	}
}