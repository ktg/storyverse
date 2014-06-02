package uk.ac.nott.mrl.stories.server;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wornchaos.parser.Parser;

import uk.ac.nott.mrl.stories.model.User;

public class ParserTests
{
	private static Parser parser;

	@BeforeClass
	public static void setup()
	{
		TestHelper.setup();
		parser = TestHelper.model.getParser();
	}

	@AfterClass
	public static void tearDown()
	{
		TestHelper.tearDown();
	}

	@Test
	public void testLongIsString()
	{
		final User user = new User("test", "test");
		user.setLastUpdate(2);

		final String encoded = parser.write(user);

		assert encoded.contains(":\"2\"");
	}
}
