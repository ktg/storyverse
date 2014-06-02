package uk.ac.nott.mrl.stories.client.ui.elements;

import org.wornchaos.client.server.HTTPException;

import uk.ac.nott.mrl.stories.model.Server;
import uk.ac.nott.mrl.stories.model.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StoryverseHeader extends Composite implements AsyncCallback<User>
{
	interface AccountPanelUiBinder extends UiBinder<Widget, StoryverseHeader>
	{
	}

	private static AccountPanelUiBinder uiBinder = GWT.create(AccountPanelUiBinder.class);

	private User user;
	private String login;

	@UiField
	Anchor anchor;

	public StoryverseHeader(final Server server)
	{
		initWidget(uiBinder.createAndBindUi(this));

		user = null;

		server.getUser(this);

		// newStory.setVisible(false);
	}

	@Override
	public void onFailure(final Throwable error)
	{
		if (error instanceof HTTPException)
		{
			final HTTPException exception = (HTTPException) error;
			if (exception.getCode() == 401)
			{
				anchor.setText("Login");
				anchor.setHref(exception.getMessage());
				login = exception.getMessage();
			}
		}
	}

	@Override
	public void onSuccess(final User user)
	{
		this.user = user;
		if (user != null)
		{
			anchor.setText(user.getName());
		}
	}

	@UiHandler("anchor")
	void navigate(final ClickEvent event)
	{
		if (user == null && login != null)
		{
			// TODO Navigate to login page
		}
		else
		{
			// TODO Navigate to author page
			// model.goto
		}
	}
}