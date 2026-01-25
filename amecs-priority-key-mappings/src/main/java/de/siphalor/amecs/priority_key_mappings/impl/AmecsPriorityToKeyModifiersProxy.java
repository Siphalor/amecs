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

import de.siphalor.amecs.key_modifiers.impl.AmecsKeyMappingManager;
import de.siphalor.amecs.key_modifiers.impl.AmecsKeyMappingManagerLayer;
import de.siphalor.amecs.priority_key_mappings.api.AmecsPriorityKeyMapping;
import lombok.NoArgsConstructor;

import com.mojang.blaze3d.platform.InputConstants;
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
