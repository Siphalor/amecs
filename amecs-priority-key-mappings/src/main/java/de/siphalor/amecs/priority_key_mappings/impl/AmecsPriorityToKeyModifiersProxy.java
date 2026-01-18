package de.siphalor.amecs.priority_key_mappings.impl;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.key_modifiers.impl.AmecsKeyMappingManager;
import de.siphalor.amecs.key_modifiers.impl.AmecsKeyMappingManagerLayer;
import de.siphalor.amecs.priority_key_mappings.api.AmecsPriorityKeyMapping;
import lombok.NoArgsConstructor;
import net.minecraft.client.KeyMapping;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AmecsPriorityToKeyModifiersProxy {
	private static final AmecsKeyMappingManagerLayer PRIORITY_LAYER = new AmecsKeyMappingManagerLayer() {
		@Override
		public boolean accepts(KeyMapping mapping) {
			return mapping instanceof AmecsPriorityKeyMapping;
		}
	};

	public static void init() {
		AmecsKeyMappingManager.prependLayer(PRIORITY_LAYER);
	}

	public static boolean onPressed(InputConstants.Key input) {
		return PRIORITY_LAYER.getMappingsForInput(input)
				.anyMatch(mapping -> ((AmecsPriorityKeyMapping) mapping).onPressedPriority());
	}

	public static boolean onReleased(InputConstants.Key input) {
		return PRIORITY_LAYER.getMappingsForInput(input)
				.anyMatch(mapping -> ((AmecsPriorityKeyMapping) mapping).onReleasedPriority());
	}
}
