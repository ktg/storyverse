package uk.ac.nott.mrl.stories.client.ui;

import org.wornchaos.client.controller.AbstractController;
import org.wornchaos.client.ui.Page;
import org.wornchaos.client.ui.StatusIndicator;
import org.wornchaos.logger.Log;

import uk.ac.nott.mrl.stories.client.Storyverse;
import uk.ac.nott.mrl.stories.client.ui.places.StoryPlace;
import uk.ac.nott.mrl.stories.model.Privacy;
import uk.ac.nott.mrl.stories.model.Server;
import uk.ac.nott.mrl.stories.model.Story;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StoryEditor extends Page<Story>
{
	interface StoryEditorUI extends UiBinder<Widget, StoryEditor>
	{
	}

	private static final StoryEditorUI ui = GWT.create(StoryEditorUI.class);

	private final AbstractController<Story> controller = new AbstractController<Story>()
	{
		@Override
		protected void save(Story value, AsyncCallback<Story> callback)
		{
			server.setStory(value, callback);
		}
	};

	@UiField
	TextArea text;

	@UiField
	TextBox title;

	@UiField
	TextBox tagText;

	@UiField
	StatusIndicator status;

	@UiField
	Panel tags;

	@UiField
	Label wordCount;

	private final Server server;

	public StoryEditor(final Server server, final String id)
	{
		this.server = server;

		if (id == null || id.equals("new") || id.equals("null"))
		{
			controller.setItem(new Story());
			getStory().setPrivacy(Privacy.Public);
		}
		else
		{
			server.getStory(id, controller);
		}

		controller.add(this);
	}

	@UiHandler("addTag")
	void addTag(final ClickEvent event)
	{
		getStory().add(tagText.getText());
		tagText.setText("");
		controller.markChanged();
	}

	@Override
	public Widget createView()
	{
		return ui.createAndBindUi(this);
	}

	@UiHandler({ "title", "text" })
	void edit(final KeyUpEvent event)
	{
		if (title.getText().equals(getStory().getTitle()) && text.getText().equals(getStory().getText())) { return; }
		getStory().setText(text.getText());
		getStory().setTitle(title.getText());
		controller.markChanged();
	}

	private Story getStory()
	{
		if (controller.getValue() == null)
		{
			Story story = new Story();
			controller.setItem(story);
			return story;
		}
		return controller.getValue();
	}

	@Override
	public void valueChanged(Story value)
	{
		Log.info("Story " + value);
		if (value == null) { return; }
		if (value.getId() != null && !Window.Location.getHash().contains(value.getId()))
		{
			Storyverse.updateURL(new StoryPlace(value));
		}

		text.setText(value.getText());
		title.setText(value.getTitle());

		tags.clear();
		for (final String tag : value.getTags())
		{
			tags.add(new Button("x " + tag, new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					getStory().remove(tag);
					controller.markChanged();
				}
			}));
		}

		updateWordCount();
	}

	private void updateWordCount()
	{
		final String s = text.getText();

		int counter = 0;

		boolean word = false;
		final int endOfLine = s.length() - 1;

		for (int i = 0; i < s.length(); i++)
		{
			// if the char is letter, word = true.
			if (Character.isLetter(s.charAt(i)) == true && i != endOfLine)
			{
				word = true;
				// if char isnt letter and there have been letters before (word
				// == true), counter goes up.
			}
			else if (Character.isLetter(s.charAt(i)) == false && word == true)
			{
				counter++;
				word = false;
				// last word of String, if it doesnt end with nonLetter it
				// wouldnt count without this.
			}
			else if (Character.isLetter(s.charAt(i)) && i == endOfLine)
			{
				counter++;
			}
		}

		wordCount.setText(counter + " words");
	}
}