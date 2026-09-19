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

package de.siphalor.amecs.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;
//- import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

/**
 * @deprecated Use {@link de.siphalor.amecs.key_modifiers.api.AmecsKeyModifier} and
 * {@link de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers} instead.
 */
@SuppressWarnings("WeakerAccess")
@Environment(EnvType.CLIENT)
@Deprecated(forRemoval = true)
public enum KeyModifier {
	// the order of the enums makes a difference when generating the shown name in the gui
	// with this order the old text order is preserved. But now the id values do not increment nicely. But changing them would eliminate
	// backward compatibility with the old save format
	NONE("none", -1),
	//# if MC_VERSION_NUMBER >= 11700
	ALT("alt", 0, InputConstants.KEY_LALT, InputConstants.KEY_RALT),
	SHIFT("shift", 2, InputConstants.KEY_LSHIFT, InputConstants.KEY_RSHIFT),
	CONTROL("control", 1, InputConstants.KEY_LCONTROL, InputConstants.KEY_RCONTROL);
	//# else
	//- ALT("alt", GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT),
	//- SHIFT("shift", 2, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT),
	//- CONTROL("control", 1, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL);
	//# end

	// using this array for the values because it is faster than calling values() every time
	public static final KeyModifier[] VALUES = KeyModifier.values();

	public final String name;
	public final int id;
	// these keyCodes are all from Type: InputUtil.Type.KEYSYM
	final int[] keyCodes;

	KeyModifier(String name, int id, int... keyCodes) {
		this.name = name;
		this.id = id;
		this.keyCodes = keyCodes;
	}

	public static KeyModifier fromKeyCode(int keyCode) {
		for (KeyModifier keyModifier : VALUES) {
			if (keyModifier == NONE) {
				continue;
			}
			if (keyModifier.matches(keyCode)) {
				return keyModifier;
			}
		}
		return NONE;
	}

	public static KeyModifier fromKey(InputConstants.Key key) {
		if (
			key == null
			//# if MC_VERSION_NUMBER >= 260300
			|| key.getType() != InputConstants.Type.KEYBOARD
			//# else
			//- || key.getType() != InputConstants.Type.KEYSYM
			//# end
		) {
			return NONE;
		}
		return fromKeyCode(key.getValue());
	}

	public boolean matches(int keyCode) {
		return ArrayUtils.contains(keyCodes, keyCode);
	}

	public String getTranslationKey() {
		return "amecsapi.modifier." + name;
	}

	public static int getModifierCount() {
		return VALUES.length - 1; // remove 1 for NONE
	}
}
