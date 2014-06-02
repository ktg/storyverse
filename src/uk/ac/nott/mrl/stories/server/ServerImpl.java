package uk.ac.nott.mrl.stories.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.wornchaos.client.server.Filter;
import org.wornchaos.client.server.PaginatedList;
import org.wornchaos.client.server.Request;
import org.wornchaos.logger.Log;
import org.wornchaos.server.JSONServerResponse;
import org.wornchaos.server.JSONServletException;

import uk.ac.nott.mrl.stories.model.Interactions;
import uk.ac.nott.mrl.stories.model.Privacy;
import uk.ac.nott.mrl.stories.model.Selection;
import uk.ac.nott.mrl.stories.model.SelectionItem;
import uk.ac.nott.mrl.stories.model.Server;
import uk.ac.nott.mrl.stories.model.Story;
import uk.ac.nott.mrl.stories.model.Storyboard;
import uk.ac.nott.mrl.stories.model.User;
import uk.ac.nott.mrl.stories.model.Vote;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ServerImpl implements Server
{
	private final Random random = new Random();
	static final ServerModel model = new ServerModel();

	private static final String[] responses = { "Yeah, Me Too", "Really?", "You're crazy", "How can you say that?",
												"Like, totes!", "Do or Do Not. There is no try" };

	@Override
	public void getSelection(final String id, final AsyncCallback<Selection> response)
	{
		response.onSuccess(model.getSelection(id));
	}

	@Override
	public void getSelectionItemUploadURL(final AsyncCallback<String> response)
	{
		final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		response.onSuccess(blobstoreService.createUploadUrl("/upload"));
	}

	@Override
	public void getSelectionResult(final String id, final AsyncCallback<SelectionItem> response)
	{
		final Selection selection = model.getSelection(id);
		final List<Vote> votes = selection.getVotes();
		final Multiset<String> voteCount = HashMultiset.create();

		for (int index = Math.max(0, votes.size() - selection.getVoteCount()); index < votes.size(); index++)
		{
			voteCount.add(votes.get(index).getItem());
		}

		String result = null;
		int resultCount = 0;
		for (final String item : voteCount.elementSet())
		{
			if (resultCount < voteCount.count(item))
			{
				result = item;
				resultCount = voteCount.count(item);
			}
		}

		if (result != null)
		{
			for (final SelectionItem item : selection.getItems())
			{
				if (item.getId().equals(result))
				{
					response.onSuccess(item);
				}
			}
		}
	}

	@Override
	public void getSelections(final AsyncCallback<Iterable<String>> response)
	{
		final List<Selection> selections = model.list(Selection.class);
		final List<String> selectionIDs = new ArrayList<String>();
		for (final Selection selection : selections)
		{
			selectionIDs.add(selection.getId());
		}

		response.onSuccess(selectionIDs);
	}

	@Override
	public void getStory(final String id, final AsyncCallback<Story> response)
	{
		if (id == null)
		{
			final List<Story> stories = model.list(Story.class);// , "draft", "false");
			if (stories.size() > 0)
			{
				final int first = random.nextInt(stories.size());
				response.onSuccess(stories.get(first));
			}
		}
		else
		{
			response.onSuccess(model.getStory(id));
		}
	}

	@Override
	public void getStoryboard(final String id, final AsyncCallback<Storyboard> response)
	{
		if (id == null || id.equals(""))
		{
			final Storyboard set = new Storyboard();

			// Get two random stories
			final List<Story> stories = model.list(Story.class, "privacy", Privacy.Public.toString());
			if (stories.size() > 0)
			{
				final int first = random.nextInt(stories.size());
				set.add(stories.get(first));

				if (stories.size() > 1)
				{
					final int second = (first + 1 + random.nextInt(stories.size() - 1)) % stories.size();
					Log.info("Story " + first + " & " + second + " of " + stories.size());
					set.add(stories.get(second));
				}
			}

			response.onSuccess(set);
		}
		else
		{
			// TODO Check draft, etc
			response.onSuccess(model.getStoryboard(id));
		}
	}

	@Override
	public void getTags(final AsyncCallback<Iterable<String>> response)
	{
		response.onSuccess(model.getTags());
	}

	@Override
	public void getUser(final AsyncCallback<User> response)
	{
		final User user = verifyUser(response);
		response.onSuccess(user);
	}

	@Override
	public boolean isOffline()
	{
		return false;
	}

	@Override
	public void listStories(final Iterable<Filter> filters, final int offset,
			final AsyncCallback<PaginatedList<Story>> response)
	{
		final int limit = 20;
		response.onSuccess(model.find(Story.class, filters, limit, offset));
	}

	@Override
	public void setOffline(final boolean offline)
	{
	}

	@Override
	public void setStory(final Story story, final AsyncCallback<Story> response)
	{
		try
		{
			model.setStory(story);

			response.onSuccess(story);
		}
		catch (final Exception e)
		{
			response.onFailure(e);
		}
	}

	@Override
	public void setStoryboard(final Storyboard storyboard, final AsyncCallback<Storyboard> response)
	{
		try
		{
			model.setStoryboard(storyboard);

			response.onSuccess(storyboard);
		}
		catch (final Exception e)
		{
			response.onFailure(e);
		}
	}

	@Override
	public void submitVote(final String id, final String image, final AsyncCallback<Selection> response)
	{
		if (id == null || id.trim().equals("") || image == null || image.trim().equals(""))
		{
			response.onFailure(new NullPointerException("id = " + id + ", image = " + image));
		}
		final Selection collection = model.getSelection(id);
		if (collection == null)
		{
			response.onFailure(new NullPointerException("Selection id = " + id + " doesn't exist"));
		}
		final String ipAddress = getIPAddress(response);
		final Vote vote = new Vote(ipAddress, image);
		collection.add(vote);
		model.setSelection(collection);
		response.onSuccess(collection);
	}

	@Override
	public void updateUser(final User user, final AsyncCallback<User> response)
	{
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("rawtypes")
	private String getHeader(final AsyncCallback<?> response, final String header)
	{
		if (response instanceof JSONServerResponse) { return ((JSONServerResponse) response).getRequest()
				.getHeader(header); }
		return null;
	}

	@SuppressWarnings("rawtypes")
	private String getIPAddress(final AsyncCallback<?> response)
	{
		if (response instanceof JSONServerResponse) { return ((JSONServerResponse) response).getRequest()
				.getRemoteAddr(); }
		return null;
	}

	private User verifyUser(final AsyncCallback<?> response)
	{
		final User user = model.getUser();
		if (user == null)
		{
			response.onFailure(new JSONServletException(HttpServletResponse.SC_UNAUTHORIZED, UserServiceFactory
					.getUserService().createLoginURL(getHeader(response, "Referer")), false));
		}
		return user;
	}

	@Override
	@Request("interact")
	public void addInteraction(String tag, AsyncCallback<String> response)
	{
		Log.info(tag);
		final String ipAddress = getIPAddress(response);
		Vote vote = new Vote(ipAddress, tag);
		Interactions interactions = model.getInteractions();
		interactions.add(vote);
		model.setInteractions(interactions);

		int responseIndex = random.nextInt(responses.length);

		response.onSuccess(responses[responseIndex]);
	}

	@Override
	@Request("clearVotes")
	public void clearVotes(@Named("id") String id, AsyncCallback<String> response)
	{
		final Selection selection = model.getSelection(id);
		selection.clearVotes();
		model.setSelection(selection);

		response.onSuccess("Success");
	}

	@Override
	@Request("selectionVoteCount")
	public void setSelectionVoteCount(@Named("id") String id, @Named("votes") int votes,
			AsyncCallback<Selection> response)
	{
		final Selection selection = model.getSelection(id);
		selection.setVoteCount(votes);
		model.setSelection(selection);

		response.onSuccess(selection);

	}
}