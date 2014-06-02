package uk.ac.nott.mrl.stories.client.ui.elements;

import org.wornchaos.client.controller.AbstractReadOnlyController;
import org.wornchaos.client.ui.CompositeView;

import uk.ac.nott.mrl.stories.model.Storyboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class StoryboardQAView extends CompositeView<Storyboard> implements HasText
{
	interface StoryboardQAViewBinder extends UiBinder<Widget, StoryboardQAView>
	{
	}

	private static final StoryboardQAViewBinder binder = GWT.create(StoryboardQAViewBinder.class);

	@UiField
	Label question;

	@UiField
	TextBox answer;

	private final AbstractReadOnlyController<Storyboard> controller;
	private boolean first;

	public StoryboardQAView(final AbstractReadOnlyController<Storyboard> controller)
	{
		this.controller = controller;
		valueChanged(controller.getValue());
	}

	@Override
	public Widget createView()
	{
		return binder.createAndBindUi(this);
	}

	@Override
	public void valueChanged(final Storyboard value)
	{
		answer.setText(getAnswer(value));
	}

	@UiHandler("answer")
	void edit(final KeyUpEvent event)
	{
		if (answer.getText().equals(getAnswer(controller.getValue()))) { return; }
		setAnswer(controller.getValue(), answer.getText());
		controller.markChanged();
	}

	public void setFirst(boolean first)
	{
		this.first = first;
	}

	private String getAnswer(final Storyboard value)
	{
		if (value == null) { return null; }
		if (first)
		{
			return value.getAnswer1();
		}
		else
		{
			return value.getAnswer2();
		}
	}

	private void setAnswer(final Storyboard storyboard, final String answer)
	{
		if (first)
		{
			storyboard.setAnswer1(answer);
		}
		else
		{
			storyboard.setAnswer2(answer);
		}
	}

	@Override
	public void focus()
	{
		answer.setFocus(true);
	}

	@Override
	public String getText()
	{
		return question.getText();
	}

	@Override
	public void setText(String text)
	{
		question.setText(text);
	}
}