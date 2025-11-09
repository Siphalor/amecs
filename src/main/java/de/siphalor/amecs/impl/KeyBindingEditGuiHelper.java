/*
 * Copyright 2020 Siphalor
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

package de.siphalor.amecs.impl;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.duck.IKeyBinding;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.KeyMapping;
//- import net.minecraft.client.Minecraft;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KeyBindingEditGuiHelper {
	public static void handleKeyPress(KeyMapping keyBinding, InputConstants.Key key) {
		if (keyBinding.isUnbound()) {
			keyBinding.setKey(key);
		} else {
			handleAdditionalKeyPress(keyBinding, key);
		}
	}

	public static void handleAdditionalKeyPress(KeyMapping keyBinding, InputConstants.Key nextKey) {
		InputConstants.Key mainKey = ((IKeyBinding) keyBinding).amecs$getBoundKey();
		KeyModifiers keyModifiers = ((IKeyBinding) keyBinding).amecs$getKeyModifiers();
		KeyModifier mainKeyModifier = KeyModifier.fromKey(mainKey);

		KeyModifier nextKeyModifier = KeyModifier.fromKey(nextKey);

		if (nextKeyModifier == KeyModifier.NONE) {
			if (mainKeyModifier != KeyModifier.NONE) {
				// swap'em around
				keyModifiers.set(mainKeyModifier, true);
				setKey(keyBinding, nextKey);
			} else {
				// shouldn't happen, as editing should already have been aborted
				setKey(keyBinding, nextKey);
			}
		} else {
			// add to existing modifiers
			keyModifiers.set(nextKeyModifier, true);
			if (mainKeyModifier != KeyModifier.NONE) {
				// special case, if e.g. both left and right shift are pressed:
				// we don't want to end up with Shift + Shift
				keyModifiers.set(mainKeyModifier, false);
			}
		}
	}

	private static void setKey(KeyMapping keyBinding, InputConstants.Key key) {
		//# if MC_VERSION_NUMBER >= 12102
		keyBinding.setKey(key);
		//# else
		//- Minecraft.getInstance().options.setKey(keyBinding, key);
		//# end
	}
}
