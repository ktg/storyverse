package uk.ac.nott.mrl.stories.server;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.nott.mrl.stories.model.Story;
import uk.ac.nott.mrl.stories.model.User;

public class DatastoreTests
{
	@BeforeClass
	public static void setup()
	{
		TestHelper.setup();
	}

	@AfterClass
	public static void tearDown()
	{
		TestHelper.tearDown();
	}

	@Test
	public void testStory()
	{
		User user = new User("1", "12");
		Story story = new Story();
		story.setAuthor(user);
		story.setText("Some text");
		story.setTitle("Title");
		story.setId(TestHelper.allocateID(Story.class));

		TestHelper.save(user);
		TestHelper.save(story);

		final Story loadedStory = TestHelper.load(Story.class, story.getId());

		assert loadedStory != null;
	}

	@Test
	public void testUser()
	{
		User user = new User("1", "12");

		TestHelper.save(user);

		final User loadedUser = TestHelper.load(User.class, user.getId());

		assert loadedUser != null;
		assert loadedUser.getId().equals(user.getId());
		assert loadedUser.getUserId().equals(user.getUserId());
	}
}