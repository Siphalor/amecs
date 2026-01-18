package de.siphalor.amecs.key_modifiers.impl;

import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Special "early" initializer for key modifiers.
 * @implNote This uses Fabric's common initializer because that runs before client initializers
 * where key mappings are usually registered.
 */
public class AmecsKeyModifiersEarlyInit implements ModInitializer {
	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			AmecsKeyModifiers.register(AmecsKeyModifiers.ALT);
			AmecsKeyModifiers.register(AmecsKeyModifiers.SHIFT);
			AmecsKeyModifiers.register(AmecsKeyModifiers.CONTROL);
			AmecsKeyModifiers.seal();
		}
	}
}
