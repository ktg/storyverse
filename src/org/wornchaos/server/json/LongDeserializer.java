package org.wornchaos.server.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LongDeserializer extends JsonDeserializer<Long>
{
	@Override
	public Long deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException,
			JsonProcessingException
	{
		final String str = jp.getText();
		final Long value = Long.parseLong(str);
		return value;
	}
}