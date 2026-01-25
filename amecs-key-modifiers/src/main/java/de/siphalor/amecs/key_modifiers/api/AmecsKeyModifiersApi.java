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

package de.siphalor.amecs.key_modifiers.api;

import de.siphalor.amecs.key_modifiers.impl.duck.IKeyMapping;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.client.KeyMapping;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AmecsKeyModifiersApi {
	/**
	 * Gets the default modifiers of the given key binding.
	 * The returned value <b>must not be modified!</b>
	 *
	 * @param mapping the key binding
	 * @return a reference to the default modifiers
	 */
	public static AmecsKeyModifierCombination getDefaultModifiers(KeyMapping mapping) {
		if (mapping instanceof AmecsKeyMappingWithKeyModifiers) {
			return ((AmecsKeyMappingWithKeyModifiers) mapping).getDefaultAmecsKeyModifiers();
		}
		return AmecsKeyModifierCombination.NO_MODIFIERS;
	}

	public static AmecsKeyModifierCombination getBoundModifiers(KeyMapping mapping) {
		return ((IKeyMapping) mapping).amecs$getBoundKeyModifiers();
	}

	public static void resetBoundModifiers(KeyMapping mapping) {
		if (mapping instanceof AmecsKeyMappingWithKeyModifiers) {
			((AmecsKeyMappingWithKeyModifiers) mapping).resetKeyBinding();
		} else {
			((IKeyMapping) mapping).amecs$getBoundKeyModifiers().unset();
		}
	}
}
