package org.wornchaos.server.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class GuavaModule extends Module // can't use just SimpleModule, due to generic types
{
	private final String NAME = "GuavaModule";

	public GuavaModule()
	{
		super();
	}

	@Override
	public String getModuleName()
	{
		return NAME;
	}

	@Override
	public void setupModule(final SetupContext context)
	{
		context.addDeserializers(new GuavaDeserializers());
		context.addSerializers(new GuavaSerializers());
		context.addTypeModifier(new MultimapTypeModifier());
	}

	@Override
	public Version version()
	{
		return ModuleVersion.instance.version();
	}
}