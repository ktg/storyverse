package org.wornchaos.server.json;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.Multimap;

/**
 * Custom deserializers module offers.
 * 
 * @author tsaloranta
 */
public class GuavaDeserializers extends Deserializers.Base
{
	@Override
	public JsonDeserializer<?> findMapLikeDeserializer(final MapLikeType type, final DeserializationConfig config,
			final BeanDescription beanDesc, final KeyDeserializer keyDeserializer,
			final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer)
			throws JsonMappingException
	{
		if (Multimap.class.isAssignableFrom(type.getRawClass())) { return new MultimapDeserializer(type,
				keyDeserializer, elementTypeDeserializer, elementDeserializer); }

		return null;
	}
}