package uk.ac.nott.mrl.stories.client.ui.elements;

import org.wornchaos.client.controller.AbstractReadOnlyController;
import org.wornchaos.client.ui.CompositeView;

import uk.ac.nott.mrl.stories.model.Story;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StoryEditView extends CompositeView<Story>
{
	interface StoryEditViewUI extends UiBinder<Widget, StoryEditView>
	{
	}

	private static final StoryEditViewUI ui = GWT.create(StoryEditViewUI.class);

	private final AbstractReadOnlyController<Story> controller;

	@UiField
	TextArea text;

	@UiField
	TextBox title;

	public StoryEditView(final AbstractReadOnlyController<Story> controller)
	{
		this.controller = controller;
		valueChanged(controller.getValue());
	}

	@Override
	public void valueChanged(final Story value)
	{
		if (value == null) { return; }
		text.setText(value.getText());
		if (value.getTitle() == null || value.getTitle().equals(""))
		{
			title.setText("Story Title");
			title.getElement().getStyle().setColor("#888");
		}
		else
		{
			title.setText(value.getTitle());
			title.getElement().getStyle().clearColor();
		}
	}

	@UiHandler("title")
	void focus(final FocusEvent event)
	{
		if (getStory().getTitle() == null || getStory().getTitle().equals(""))
		{
			title.getElement().getStyle().clearColor();
			title.setText("");
		}
	}

	@UiHandler("title")
	void blur(final BlurEvent event)
	{
		if (getStory().getTitle() == null || getStory().getTitle().equals(""))
		{
			title.setText("Story Title");
			title.getElement().getStyle().setColor("#888");
		}
		edit(null);
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
	public Widget createView()
	{
		return ui.createAndBindUi(this);
	}

	@Override
	public void focus()
	{
		text.setFocus(true);
	}
}