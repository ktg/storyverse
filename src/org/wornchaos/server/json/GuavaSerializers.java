package org.wornchaos.server.json;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.Multimap;

public class GuavaSerializers extends Serializers.Base
{
	@Override
	public JsonSerializer<?> findMapLikeSerializer(final SerializationConfig config, final MapLikeType type,
			final BeanDescription beanDesc, final JsonSerializer<Object> keySerializer,
			final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer)
	{
		if (Multimap.class.isAssignableFrom(type.getRawClass())) { return new MultimapSerializer(config, type,
				beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer); }
		return null;
	}
}