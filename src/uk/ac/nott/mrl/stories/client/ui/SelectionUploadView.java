package uk.ac.nott.mrl.stories.client.ui;

import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.client.ui.Page;
import org.wornchaos.client.ui.ViewCallback;
import org.wornchaos.client.ui.layout.HorizontalLayout;
import org.wornchaos.client.ui.layout.LayoutPanel;
import org.wornchaos.logger.Log;

import uk.ac.nott.mrl.stories.client.Storyverse;
import uk.ac.nott.mrl.stories.client.ui.elements.SelectionItemView;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionUploadPlace;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionsPlace;
import uk.ac.nott.mrl.stories.model.Selection;
import uk.ac.nott.mrl.stories.model.SelectionItem;
import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SelectionUploadView extends Page<Selection>
{
	interface ImageUploadViewUiBinder extends UiBinder<Widget, SelectionUploadView>
	{
	}

	private static ImageUploadViewUiBinder uiBinder = GWT.create(ImageUploadViewUiBinder.class);

	@UiField
	Button uploadButton;

	@UiField
	FormPanel uploadForm;

	@UiField
	FileUpload uploadField;

	@UiField
	Hidden collectionID;

	@UiField
	LayoutPanel itemPanel;

	@UiField
	TextBox voteCount;

	private String id;

	private final Server server;

	public SelectionUploadView(final Server server, final String id)
	{
		this.server = server;
		this.id = id;
	}

	@UiHandler("uploadButton")
	void onSubmit(final ClickEvent e)
	{
		uploadForm.submit();
	}

	@UiHandler("clearVotes")
	void onClearVotes(final ClickEvent e)
	{
		server.clearVotes(id, new AsyncCallback<String>()
		{
			@Override
			public void onSuccess(String response)
			{
				Log.info("Successfully Cleared Votes");
			}
		});
	}

	@UiHandler("voteCount")
	void votesChanged(final BlurEvent event)
	{
		try
		{
			int votes = Integer.parseInt(voteCount.getText());

			server.setSelectionVoteCount(id, votes, new ViewCallback<Selection>(this));
		}
		catch (Exception e)
		{
			Log.error(e);
		}
	}

	@Override
	public Widget createView()
	{
		final Widget widget = uiBinder.createAndBindUi(this);

		itemPanel.setLayout(new HorizontalLayout(3));

		// Disable the button until we get the URL to POST to
		uploadButton.setText("Loading...");
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadButton.setEnabled(false);
		uploadField.setName("image");

		collectionID.setName("id");
		collectionID.setValue(id);

		// Now we use out GWT-RPC service and get an URL
		startNewBlobstoreSession();

		// Once we've hit submit and it's complete, let's set the form to a new session.
		// We could also have probably done this on the onClick handler
		uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler()
		{
			@Override
			public void onSubmitComplete(final SubmitCompleteEvent event)
			{
				uploadForm.reset();

				Log.info("Form Result: " + event.getResults());

				if (event.getResults() == null)
				{
					Storyverse.goTo(new SelectionsPlace());
				}
				else
				{
					setID(event.getResults());
					startNewBlobstoreSession();
				}
			}
		});

		return widget;
	}

	private void setID(final String id)
	{
		if (id == null || id.equals(this.id)) { return; }

		Log.info("\"" + id + "\"");

		this.id = id;
		collectionID.setValue(id);
		Storyverse.updateURL(new SelectionUploadPlace(id));
	}

	private void startNewBlobstoreSession()
	{
		if (id != null && !id.equals("new"))
		{
			server.getSelection(id, new ViewCallback<Selection>(this));
		}

		server.getSelectionItemUploadURL(new AsyncCallback<String>()
		{
			@Override
			public void onSuccess(final String response)
			{
				uploadForm.setAction(response);
				uploadButton.setText("Upload");
				uploadButton.setEnabled(true);
			}
		});
	}

	@Override
	public void valueChanged(Selection collection)
	{
		itemPanel.clear();
		if (collection == null) { return; }
		setID(collection.getId());
		voteCount.setText(Integer.toString(collection.getVoteCount()));
		for (final SelectionItem item : collection.getItems())
		{
			final SelectionItemView view = new SelectionItemView(false, false);
			view.valueChanged(item);
			itemPanel.add(view);
		}
	}
}