package de.siphalor.amecs.key_modifiers.impl;

import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
//- import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers;
import de.siphalor.amecs.key_modifiers.impl.compat.controlling.AmecsControllingIntegration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.CustomLog;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.KeyMapping;

@CustomLog
public class AmecsKeyModifiersModule implements ClientModInitializer {
	public static final String MOD_ID = "amecs_key_modifiers";
	public static final AmecsKeyModifierCombination CURRENT_MODIFIERS = new AmecsKeyModifierCombination();

	private static Map<String, KeyMapping> idToKeyBindingMap;

	@Override
	public void onInitializeClient() {
		//# if CONTROLLING_INTEGRATION
		FabricLoader loader = FabricLoader.getInstance();
		if (loader.isModLoaded("controlling")) {
			AmecsControllingIntegration.initialize();
		}
		//# end
	}

	/**
	 * Gets the "official" idToKeys map
	 *
	 * @return the map (use with care)
	 */
	public static Map<String, KeyMapping> getIdToKeyBindingMap() {
		if (idToKeyBindingMap == null) {
			try {
				// reflections accessors should be initialized statically if they are static
				// but in this case its fine because we only do this once because it is cached in a static field

				// noinspection JavaReflectionMemberAccess
				Method method = KeyMapping.class.getDeclaredMethod("amecs$getIdToKeyBindingMap");
				method.setAccessible(true);
				// noinspection unchecked
				idToKeyBindingMap = (Map<String, KeyMapping>) method.invoke(null);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				log.error("Failed to get access to key bindings", e);
			}
		}
		return idToKeyBindingMap;
	}
}
