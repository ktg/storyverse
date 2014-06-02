package uk.ac.nott.mrl.stories.server;

import uk.ac.nott.mrl.stories.model.Story;
import uk.ac.nott.mrl.stories.model.User;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class TestHelper
{
	public static final String MINION_NAME = "Testy McTesterson";

	private static final Objectify ofy = ObjectifyService.factory().begin();
	public static final ServerModel model = new ServerModel();

	private static final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	public static String allocateID(final Class<?> clazz)
	{
		return Long.toString(ofy.getFactory().allocateId(clazz).getId());
	}

	public static long allocateLongID(final Class<?> clazz)
	{
		return ofy.getFactory().allocateId(clazz).getId();
	}

	public static <T> T load(final Class<T> clazz, final long id)
	{
		return ofy.load().type(clazz).id(id).get();
	}

	public static <T> T load(final Class<T> clazz, final String id)
	{
		return ofy.load().type(clazz).id(id).get();
	}

	public static void save(final Object entity)
	{
		ofy.save().entity(entity).now();
	}

	public static void setup()
	{
		ObjectifyService.register(User.class);
		ObjectifyService.register(Story.class);

		helper.setEnvAuthDomain("Test");
		helper.setEnvEmail("test@wornchaos.org");
		helper.setEnvIsLoggedIn(true);
		helper.setUp();
	}

	public static void tearDown()
	{
		helper.tearDown();
	}
}