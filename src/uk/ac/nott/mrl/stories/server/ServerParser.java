package uk.ac.nott.mrl.stories.server;

import org.wornchaos.logger.Log;
import org.wornchaos.parser.Parser;
import org.wornchaos.server.json.LongDeserializer;
import org.wornchaos.server.json.LongSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ServerParser implements Parser
{
	private static final ObjectMapper mapper = new ObjectMapper();

	static
	{
		final SimpleModule module = new SimpleModule("Stories");
		module.addSerializer(long.class, new LongSerializer());
		module.addDeserializer(long.class, new LongDeserializer());
		module.addSerializer(Long.class, new LongSerializer());
		module.addDeserializer(Long.class, new LongDeserializer());
		mapper.registerModule(module);

		mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setVisibilityChecker(new VisibilityChecker.Std(JsonAutoDetect.Visibility.NONE,
				JsonAutoDetect.Visibility.NONE, JsonAutoDetect.Visibility.NONE, JsonAutoDetect.Visibility.NONE,
				JsonAutoDetect.Visibility.ANY));
	}

	@Override
	public <T> T parse(final Class<T> clazz, final String string)
	{
		try
		{
			Log.info("Parsing " + string);
			return mapper.readValue(string, clazz);
		}
		catch (final Exception e)
		{
			Log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public String write(final Object object)
	{
		try
		{
			return mapper.writeValueAsString(object);
		}
		catch (final Exception e)
		{
			Log.error(e.getMessage(), e);
			return null;
		}
	}
}