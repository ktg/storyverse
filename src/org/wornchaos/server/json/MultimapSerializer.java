package org.wornchaos.server.json;

import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class MultimapSerializer extends JsonSerializer<Multimap<?, ?>> implements ContextualSerializer
{
	private final MapLikeType type;
	private final BeanProperty property;
	private final JsonSerializer<Object> keySerializer;
	private final TypeSerializer valueTypeSerializer;
	private final JsonSerializer<Object> valueSerializer;

	public MultimapSerializer(final SerializationConfig config, final MapLikeType type, final BeanDescription beanDesc,
			final JsonSerializer<Object> keySerializer, final TypeSerializer valueTypeSerializer,
			final JsonSerializer<Object> valueSerializer)
	{
		this.type = type;
		property = null;
		this.keySerializer = keySerializer;
		this.valueTypeSerializer = valueTypeSerializer;
		this.valueSerializer = valueSerializer;
	}

	@SuppressWarnings("unchecked")
	protected MultimapSerializer(final MultimapSerializer src, final BeanProperty property,
			final JsonSerializer<?> keySerializer, final TypeSerializer valueTypeSerializer,
			final JsonSerializer<?> valueSerializer)
	{
		type = src.type;
		this.property = property;
		this.keySerializer = (JsonSerializer<Object>) keySerializer;
		this.valueTypeSerializer = valueTypeSerializer;
		this.valueSerializer = (JsonSerializer<Object>) valueSerializer;
	}

	@Override
	public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property)
			throws JsonMappingException
	{
		JsonSerializer<?> valueSer = valueSerializer;
		if (valueSer == null)
		{ // if type is final, can actually resolve:
			final JavaType valueType = type.getContentType();
			if (valueType.isFinal())
			{
				valueSer = provider.findValueSerializer(valueType, property);
			}
		}
		else if (valueSer instanceof ContextualSerializer)
		{
			valueSer = ((ContextualSerializer) valueSer).createContextual(provider, property);
		}
		JsonSerializer<?> keySer = keySerializer;
		if (keySer == null)
		{
			keySer = provider.findKeySerializer(type.getKeyType(), property);
		}
		else if (keySer instanceof ContextualSerializer)
		{
			keySer = ((ContextualSerializer) keySer).createContextual(provider, property);
		}
		// finally, TypeSerializers may need contextualization as well
		TypeSerializer typeSer = valueTypeSerializer;
		if (typeSer != null)
		{
			typeSer = typeSer.forProperty(property);
		}
		return withResolved(property, keySer, typeSer, valueSer);
	}

	/*
	 * /********************************************************** /* Post-processing
	 * (contextualization) /**********************************************************
	 */

	@Override
	public void serialize(final Multimap<?, ?> value, final JsonGenerator jgen, final SerializerProvider provider)
			throws IOException, JsonProcessingException
	{
		jgen.writeStartObject();
		if (!value.isEmpty())
		{
			serializeFields(value, jgen, provider);
		}
		jgen.writeEndObject();
	}

	/*
	 * /********************************************************** /* JsonSerializer implementation
	 * /**********************************************************
	 */

	@Override
	public void serializeWithType(final Multimap<?, ?> value, final JsonGenerator jgen,
			final SerializerProvider provider, final TypeSerializer typeSer) throws IOException,
			JsonGenerationException
	{
		typeSer.writeTypePrefixForObject(value, jgen);
		serializeFields(value, jgen, provider);
		typeSer.writeTypeSuffixForObject(value, jgen);
	}

	protected MultimapSerializer withResolved(final BeanProperty property, final JsonSerializer<?> keySerializer,
			final TypeSerializer valueTypeSerializer, final JsonSerializer<?> valueSerializer)
	{
		return new MultimapSerializer(this, property, keySerializer, valueTypeSerializer, valueSerializer);
	}

	private final void serializeFields(final Multimap<?, ?> value, final JsonGenerator jgen,
			final SerializerProvider provider) throws IOException, JsonProcessingException
	{
		for (final Entry<?, ? extends Collection<?>> e : value.asMap().entrySet())
		{
			if (keySerializer != null)
			{
				keySerializer.serialize(e.getKey(), jgen, provider);
			}
			else
			{
				provider.findKeySerializer(provider.constructType(String.class), property).serialize(e.getKey(), jgen,
																										provider);
			}
			if (valueSerializer != null)
			{
				// note: value is a List, but generic type is for contents... so:
				jgen.writeStartArray();
				for (final Object vv : e.getValue())
				{
					valueSerializer.serialize(vv, jgen, provider);
				}
				jgen.writeEndArray();
			}
			else
			{
				provider.defaultSerializeValue(Lists.newArrayList(e.getValue()), jgen);
			}
		}
	}
}