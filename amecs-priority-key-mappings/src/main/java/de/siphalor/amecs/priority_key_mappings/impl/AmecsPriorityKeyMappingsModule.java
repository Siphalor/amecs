/*
 * Copyright 2026 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.amecs.priority_key_mappings.impl;

import de.siphalor.amecs.priority_key_mappings.api.AmecsPriorityKeyMapping;
import de.siphalor.amecs.priority_key_mappings.impl.mixin.KeyMappingAccessor;
import java.util.Collections;
import java.util.List;
//- import java.util.Optional;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

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
			return getMappingsForKeyVanilla(key).stream()
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
			return getMappingsForKeyVanilla(key).stream()
					.anyMatch(mapping -> {
						if (!(mapping instanceof AmecsPriorityKeyMapping)) {
							return false;
						}
						return ((AmecsPriorityKeyMapping) mapping).onReleasedPriority();
					});
		}
	}

	private static List<KeyMapping> getMappingsForKeyVanilla(InputConstants.Key key) {
		//# if MC_VERSION_NUMBER >= 12109
		return KeyMappingAccessor.getMAP().getOrDefault(key, Collections.emptyList());
		//# else
		//- return Optional.ofNullable(KeyMappingAccessor.getMAP().get(key))
		//- 		.map(Collections::singletonList)
		//- 		.orElse(Collections.emptyList());
		//# end
	}
}
