package org.wornchaos.server.json;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

public class SimpleTypeResolverBuilder extends StdTypeResolverBuilder
{
	@Override
	protected TypeIdResolver idResolver(final MapperConfig<?> config, final JavaType baseType,
			final Collection<NamedType> subtypes, final boolean forSer, final boolean forDeser)
	{
		if (_idType == Id.NAME) { return new SimpleTypeIdResolver(baseType, config.getTypeFactory()); }
		return super.idResolver(config, baseType, subtypes, forSer, forDeser);
	}
}
