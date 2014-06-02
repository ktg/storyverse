package uk.ac.nott.mrl.stories.model;

import javax.inject.Named;

import org.wornchaos.client.server.Cache;
import org.wornchaos.client.server.Filter;
import org.wornchaos.client.server.JSONServer;
import org.wornchaos.client.server.PaginatedList;
import org.wornchaos.client.server.Request;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Server extends JSONServer
{
	@Request("selection")
	public void getSelection(@Named("id") final String id, final AsyncCallback<Selection> response);

	@Request("uploadURL")
	public void getSelectionItemUploadURL(final AsyncCallback<String> response);

	@Request("selectionResult")
	public void getSelectionResult(@Named("id") final String id, final AsyncCallback<SelectionItem> response);

	@Request("selections")
	public void getSelections(final AsyncCallback<Iterable<String>> response);

	@Request("story")
	public void getStory(@Named("id") final String id, final AsyncCallback<Story> response);

	@Request("storyboard")
	public void getStoryboard(@Named("id") final String id, final AsyncCallback<Storyboard> response);

	@Request("clearVotes")
	public void clearVotes(@Named("id") final String id, final AsyncCallback<String> response);

	@Request("selectionVoteCount")
	public void setSelectionVoteCount(@Named("id") final String id, @Named("votes") final int votes,
			final AsyncCallback<Selection> response);

	@Request("tags")
	public void getTags(final AsyncCallback<Iterable<String>> response);

	@Request("interact")
	public void addInteraction(@Named("tag") final String tag, final AsyncCallback<String> response);

	@Cache
	@Request("user")
	public void getUser(final AsyncCallback<User> response);

	@Request("stories")
	public void listStories(@Named("filters") final Iterable<Filter> filters, @Named("offset") final int offset,
			final AsyncCallback<PaginatedList<Story>> response);

	@Request("saveStory")
	public void setStory(@Named("story") final Story story, final AsyncCallback<Story> response);

	@Request("saveStoryboard")
	public void setStoryboard(@Named("storyboard") final Storyboard storyboard, final AsyncCallback<Storyboard> response);

	public void submitVote(@Named("id") final String id, @Named("image") final String image,
			final AsyncCallback<Selection> response);

	public void updateUser(@Named("user") final User user, final AsyncCallback<User> response);
}
