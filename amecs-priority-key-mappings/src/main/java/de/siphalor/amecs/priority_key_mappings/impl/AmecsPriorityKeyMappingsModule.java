package de.siphalor.amecs.priority_key_mappings.impl;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.priority_key_mappings.api.AmecsPriorityKeyMapping;
import de.siphalor.amecs.priority_key_mappings.impl.mixin.KeyMappingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
//- import net.minecraft.client.KeyMapping;

import java.util.Collections;

public class AmecsPriorityKeyMappingsModule implements ClientModInitializer {
	private static final boolean KEY_MODIFIERS_MODULE_PRESENT = FabricLoader.getInstance().isModLoaded("amecs_key_modifiers");

	@Override
	public void onInitializeClient() {
		if (KEY_MODIFIERS_MODULE_PRESENT) {
			AmecsPriorityToKeyModifiersProxy.init();
		}
	}

	public static boolean onPressedPriority(InputConstants.Key key) {
		if (KEY_MODIFIERS_MODULE_PRESENT) {
			return AmecsPriorityToKeyModifiersProxy.onPressed(key);
		} else {
			return KeyMappingAccessor.getMAP().getOrDefault(key, Collections.emptyList()).stream()
					.anyMatch(mapping -> {
						if (!(mapping instanceof AmecsPriorityKeyMapping)) {
							return false;
						}
						return ((AmecsPriorityKeyMapping) mapping).onPressedPriority();
					});
		}
	}

	public static boolean onReleasedPriority(InputConstants.Key key) {
		if (KEY_MODIFIERS_MODULE_PRESENT) {
			return AmecsPriorityToKeyModifiersProxy.onReleased(key);
		} else {
			return KeyMappingAccessor.getMAP().getOrDefault(key, Collections.emptyList()).stream()
					.anyMatch(mapping -> {
						if (!(mapping instanceof AmecsPriorityKeyMapping)) {
							return false;
						}
						return ((AmecsPriorityKeyMapping) mapping).onReleasedPriority();
					});
		}
	}
}
