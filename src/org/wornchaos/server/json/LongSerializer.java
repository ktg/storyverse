package org.wornchaos.server.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LongSerializer extends JsonSerializer<Long>
{
	@Override
	public void serialize(final Long value, final JsonGenerator jgen, final SerializerProvider provider)
			throws IOException, JsonProcessingException
	{
		jgen.writeString("" + value.toString());
	}
}