package uk.ac.nott.mrl.stories.client.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.wornchaos.client.ui.Page;
import org.wornchaos.client.ui.layout.LayoutPanel;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public class TagView extends Page<Collection<String>>
{
	private LayoutPanel panel;

	private Map<String, Hyperlink> links = new HashMap<String, Hyperlink>();

	@Override
	public void valueChanged(Collection<String> value)
	{
		for (String tag : value)
		{
			if (links.containsKey(tag))
			{

			}
		}
	}

	@Override
	public Widget createView()
	{
		panel = new LayoutPanel();
		return panel;
	}

}
