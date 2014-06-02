package uk.ac.nott.mrl.stories.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.wornchaos.client.server.Filter;
import org.wornchaos.client.server.PaginatedList;
import org.wornchaos.logger.Log;
import org.wornchaos.parser.Parser;

import uk.ac.nott.mrl.stories.model.Interactions;
import uk.ac.nott.mrl.stories.model.Selection;
import uk.ac.nott.mrl.stories.model.SelectionItem;
import uk.ac.nott.mrl.stories.model.Story;
import uk.ac.nott.mrl.stories.model.Storyboard;
import uk.ac.nott.mrl.stories.model.Tags;
import uk.ac.nott.mrl.stories.model.User;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

public class ServerModel
{
	static
	{
		ObjectifyService.register(User.class);
		ObjectifyService.register(Story.class);
		ObjectifyService.register(Selection.class);
		ObjectifyService.register(SelectionItem.class);
		ObjectifyService.register(Storyboard.class);
		ObjectifyService.register(Interactions.class);
	}

	private final static long timestampOffset = new Date().getTime() / 100;

	public static int getTimestamp()
	{
		final long now = new Date().getTime() / 100;
		return (int) (now - timestampOffset);
	}

	private final Parser parser = new ServerParser();

	private final Objectify ofy = ObjectifyService.factory().begin();

	public String allocateID(final Class<?> clazz)
	{
		return Long.toString(ofy.getFactory().allocateId(clazz).getId());
	}

	public <T> PaginatedList<T> find(final Class<T> clazz, final Iterable<Filter> filters, final int limit,
			final int offset)
	{
		Query<T> items = ofy.load().type(clazz);
		Log.warn("Filtered " + filters);
		if (filters != null)
		{
			for (final Filter filter : filters)
			{
				items = items.filter(filter.getCondition(), filter.getValue());
				Log.warn("Filtered " + filter.getCondition() + " " + filter.getValue() + ": " + items.list().size());
			}
		}
		final PaginatedList<T> page = new PaginatedList<T>(offset, items.count(), limit);
		final List<T> list = items.offset(offset).limit(limit).list();
		for (final T item : list)
		{
			page.add(item);
		}
		return page;
	}

	public Parser getParser()
	{
		return parser;
	}

	public Selection getSelection(final String id)
	{
		if (id == null) { return null; }
		return ofy.load().type(Selection.class).id(id).get();
	}

	public Story getStory(final String id)
	{
		if (id == null) { return null; }
		return ofy.load().type(Story.class).id(id).get();
	}

	public Collection<String> getTags()
	{
		final Tags tags = ofy.load().type(Tags.class).first().get();

		if (tags != null)
		{
			return tags.getTags();
		}
		else
		{
			final Tags newTags = new Tags();
			setTags(newTags);
			return newTags.getTags();
		}
	}

	private void setTags(Tags tags)
	{
		if (tags.getId() == null)
		{
			tags.setId(allocateID(Tags.class));
		}
		ofy.save().entity(tags);
	}

	public void setTags(Collection<String> tagList)
	{
		Tags tags = ofy.load().type(Tags.class).first().get();

		if (tags != null)
		{
			tags.setTags(tagList);
		}
		else
		{
			tags = new Tags();
			tags.setTags(tagList);
		}
		setTags(tags);
	}

	public Storyboard getStoryboard(final String id)
	{
		if (id == null) { return null; }
		return ofy.load().type(Storyboard.class).id(id).get();
	}

	public SelectionItem getMedia(final String id)
	{
		if (id == null) { return null; }
		return ofy.load().type(SelectionItem.class).id(id).get();
	}

	public User getUser()
	{
		final UserService userService = UserServiceFactory.getUserService();
		final com.google.appengine.api.users.User user = userService.getCurrentUser();
		if (user == null)
		{
			Log.info("No user logged in");
			return null;
		}

		final List<User> users = find(User.class, "userId", user.getUserId());
		if (users == null || users.isEmpty())
		{
			final User result = new User(allocateID(User.class), user.getUserId());
			store(result);
			return result;
		}
		else if (users.size() > 1)
		{
			throw new OutOfMemoryError();
		}
		else
		{
			// Log.info("Got User: ", userEntities.get(0), parser);
			return users.get(0);
		}
	}

	public Interactions getInteractions()
	{
		final Interactions interactions = ofy.load().type(Interactions.class).first().get();

		if (interactions != null)
		{
			return interactions;
		}
		else
		{
			final Interactions newInteractions = new Interactions();
			setInteractions(newInteractions);
			return newInteractions;
		}
	}

	public void setInteractions(Interactions interactions)
	{
		if (interactions.getId() == null)
		{
			interactions.setId(allocateID(Interactions.class));
		}
		ofy.save().entity(interactions);

	}

	public <T> List<T> list(final Class<T> clazz, final String... params)
	{
		Query<T> items = ofy.load().type(clazz);
		if (params != null)
		{
			for (int index = 0; index < params.length; index += 2)
			{
				items = items.filter(params[index], params[index + 1]);
				Log.warn("Filtered " + params[index] + " " + params[index + 1] + ": " + items.list().size());

			}
		}
		return items.list();
	}

	public <T> List<T> list(final T... items)
	{
		final List<T> result = new ArrayList<T>();
		for (final T item : items)
		{
			result.add(item);
		}
		return result;
	}

	public void setSelection(final Selection collection)
	{
		if (collection.getId() == null)
		{
			collection.setId(allocateID(Selection.class));
		}
		store(collection);
	}

	public void setStory(final Story story)
	{
		final User user = getUser();
		if (user == null) { throw new RuntimeException("Not Logged In"); }

		if (story.getAuthor() == null)
		{
			story.setAuthor(user);
		}
		// TODO More thoroughly check permissions/depending on exact changes
		// eg. Other people can change tags, but not text
		else if (!story.getAuthor().getId().equals(user.getId())) { throw new RuntimeException("Not Author "
				+ story.getAuthor().getId() + "!=" + user.getId()); }

		if (story.getId() == null)
		{
			story.setId(allocateID(Story.class));
		}
		store(story);
	}

	public void setStoryboard(final Storyboard storyboard)
	{
		for (final Story story : storyboard.getStories())
		{
			setStory(story);
		}

		if (storyboard.getStory() != null)
		{
			setStory(storyboard.getStory());
		}

		if (storyboard.getId() == null)
		{
			storyboard.setId(allocateID(Storyboard.class));
		}

		store(storyboard);
	}

	private <T> List<T> find(final Class<T> clazz, final String query, final Object value)
	{
		return ofy.load().type(clazz).filter(query, value).list();
	}

	private void store(final Object obj)
	{
		// Log.info("Store: ", obj, parser);
		ofy.save().entity(obj).now();
	}

	public void setMedia(SelectionItem image)
	{
		if (image.getId() == null)
		{
			image.setId(allocateID(SelectionItem.class));
		}
		store(image);

	}
}