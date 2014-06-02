package org.wornchaos.server.json;

import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import com.google.common.collect.Multimap;

public class MultimapTypeModifier extends TypeModifier
{
	@Override
	public JavaType modifyType(final JavaType type, final Type jdkType, final TypeBindings context,
			final TypeFactory typeFactory)
	{
		if (Multimap.class.isAssignableFrom(type.getRawClass()))
		{
			JavaType keyType = type.containedType(0);
			JavaType contentType = type.containedType(1);

			if (keyType == null)
			{
				keyType = typeFactory.constructType(String.class);
			}

			if (contentType == null)
			{
				contentType = typeFactory.constructType(Object.class);
			}

			return typeFactory.constructMapLikeType(type.getRawClass(), keyType, contentType);
		}

		return type;
	}

}