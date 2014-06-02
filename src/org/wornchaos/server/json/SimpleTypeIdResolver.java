package org.wornchaos.server.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class SimpleTypeIdResolver extends ClassNameIdResolver
{
	protected String _basePackagePrefix;

	public SimpleTypeIdResolver()
	{
		super(null, null);
	}

	protected SimpleTypeIdResolver(final JavaType baseType, final TypeFactory typeFactory)
	{
		super(baseType, typeFactory);
		final String base = baseType.getRawClass().getName();
		final int ix = base.lastIndexOf('.');
		if (ix < 0)
		{
			_basePackagePrefix = "";
		}
		else
		{
			_basePackagePrefix = base.substring(0, ix + 1);
		}
	}

	@Override
	public JsonTypeInfo.Id getMechanism()
	{
		return JsonTypeInfo.Id.NAME;
	}

	@Override
	public String idFromValue(final Object value)
	{
		final String n = value.getClass().getName();
		if (n.startsWith(_basePackagePrefix)) { return n.substring(_basePackagePrefix.length()); }
		return n;
	}

	@Override
	public JavaType typeFromId(String id)
	{
		final StringBuilder sb = new StringBuilder(id.length() + _basePackagePrefix.length());
		if (_basePackagePrefix.length() == 0)
		{
			// no package; must remove leading '.' from id
			sb.append(id.substring(1));
		}
		else
		{
			// otherwise just concatenate package, with leading-dot-partial name
			sb.append(_basePackagePrefix).append(id);
		}
		id = sb.toString();
		return super.typeFromId(id);
	}
}