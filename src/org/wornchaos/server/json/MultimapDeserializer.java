package org.wornchaos.server.json;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class MultimapDeserializer extends JsonDeserializer<Multimap<?, ?>> implements ContextualDeserializer
{
	private static final List<Class<?>> KNOWN_IMPLEMENTATIONS = ImmutableList
			.<Class<?>> of(ImmutableListMultimap.class, ImmutableSetMultimap.class, ImmutableMultimap.class);
	private static final List<String> METHOD_NAMES = ImmutableList.of("create", "copyOf");

	private final MapLikeType type;
	private final KeyDeserializer keyDeserializer;
	private final TypeDeserializer elementTypeDeserializer;
	private final JsonDeserializer<?> elementDeserializer;

	public MultimapDeserializer(final MapLikeType type, final KeyDeserializer keyDeserializer,
			final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer)
			throws JsonMappingException
	{
		this.type = type;
		this.keyDeserializer = keyDeserializer;
		this.elementTypeDeserializer = elementTypeDeserializer;
		this.elementDeserializer = elementDeserializer;
	}

	/**
	 * We need to use this method to properly handle possible contextual variants of key and value
	 * deserializers, as well as type deserializers.
	 */
	@Override
	public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property)
			throws JsonMappingException
	{
		KeyDeserializer kd = keyDeserializer;
		if (kd == null)
		{
			kd = ctxt.findKeyDeserializer(type.getKeyType(), property);
		}
		JsonDeserializer<?> ed = elementDeserializer;
		if (ed == null)
		{
			ed = ctxt.findContextualValueDeserializer(type.getContentType(), property);
		}
		// Type deserializer is slightly different; must be passed, but needs to become contextual:
		final TypeDeserializer etd = elementTypeDeserializer;
		if (etd != null)
		{
			// etd = etd.forProperty(property);
		}
		return new MultimapDeserializer(type, kd, etd, ed);
	}

	@Override
	public Multimap<?, ?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException,
			JsonProcessingException
	{
		// Picked LLM since it is preserves both K, V ordering and supports nulls.
		final LinkedListMultimap<Object, Object> builder = LinkedListMultimap.create();

		while (jp.nextToken() != JsonToken.END_OBJECT)
		{
			final Object key;
			if (keyDeserializer != null)
			{
				key = keyDeserializer.deserializeKey(jp.getCurrentName(), ctxt);
			}
			else
			{
				key = jp.getCurrentName();
			}

			jp.nextToken();
			expect(jp, JsonToken.START_ARRAY);

			while (jp.nextToken() != JsonToken.END_ARRAY)
			{
				if (elementDeserializer != null)
				{
					builder.put(key, elementDeserializer.deserializeWithType(jp, ctxt, elementTypeDeserializer));
				}
				else
				{
					builder.put(key, elementDeserializer.deserialize(jp, ctxt));
				}
			}
		}

		return transform(type.getRawClass(), builder);
	}

	private void expect(final JsonParser jp, final JsonToken token) throws IOException
	{
		if (jp.getCurrentToken() != token) { throw new JsonMappingException("Expecting " + token + ", found "
				+ jp.getCurrentToken(), jp.getCurrentLocation()); }
	}

	private Multimap<?, ?> transform(final Class<?> rawClass, final Multimap<Object, Object> map)
			throws JsonMappingException
	{
		// TODO: this is reflective and probably a bit slow. Given the sheer number of
		// Multimap implementations, writing it out by hand is probably a lot of code.
		// A better approach would be a nice optimization (scs 1/30/2012)
		final LinkedList<Class<?>> classesToTry = Lists.newLinkedList(KNOWN_IMPLEMENTATIONS);
		classesToTry.addFirst(rawClass);

		for (final Class<?> klass : classesToTry)
		{
			for (final String methodName : METHOD_NAMES)
			{
				try
				{
					final Method m = klass.getMethod(methodName, Multimap.class);
					return (Multimap<?, ?>) m.invoke(null, map);
				}
				catch (final SecurityException e)
				{
					throw new JsonMappingException("Could not map to " + klass, e);
				}
				catch (final NoSuchMethodException e)
				{
				}
				catch (final IllegalAccessException e)
				{
				}
				catch (final InvocationTargetException e)
				{
					throw new JsonMappingException("Could not map to " + klass, e);
				}
			}
		}

		// If everything goes wrong, just give them the LinkedListMultimap...
		return map;
	}
}