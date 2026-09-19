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

import de.siphalor.amecs.key_modifiers.impl.DefaultKeyModifier;
import java.util.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
//- import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AmecsKeyModifiers {
	private static final Map<String, AmecsKeyModifier> MODIFIERS_BY_NAME = new HashMap<>();
	static final List<AmecsKeyModifier> MODIFIERS = new ArrayList<>(5);
	private static boolean sealed = false;

	//# if MC_VERSION_NUMBER >= 11700
	public static AmecsKeyModifier ALT = new DefaultKeyModifier("alt", 0, InputConstants.KEY_LALT, InputConstants.KEY_RALT);
	public static AmecsKeyModifier SHIFT = new DefaultKeyModifier("shift", 2, InputConstants.KEY_LSHIFT, InputConstants.KEY_RSHIFT);
	public static AmecsKeyModifier CONTROL = new DefaultKeyModifier("control", 1, InputConstants.KEY_LCONTROL, InputConstants.KEY_RCONTROL);
	//# else
	//- public static AmecsKeyModifier ALT = new DefaultKeyModifier("alt", 0, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT);
	//- public static AmecsKeyModifier SHIFT = new DefaultKeyModifier("shift", 2, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT);
	//- public static AmecsKeyModifier CONTROL = new DefaultKeyModifier("control", 1, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL);
	//# end

	public static void register(AmecsKeyModifier keyModifier) {
		requireUnsealed();

		keyModifier.arrayIndex = MODIFIERS.size();
		MODIFIERS_BY_NAME.put(keyModifier.getName(), keyModifier);
		MODIFIERS.add(keyModifier);
	}

	@ApiStatus.Internal
	public static void seal() {
		requireUnsealed();

		sealed = true;
	}

	public static Collection<AmecsKeyModifier> getAll() {
		requireSealed();

		return MODIFIERS;
	}

	public static @Nullable AmecsKeyModifier getByName(String name) {
		return MODIFIERS_BY_NAME.get(name);
	}

	public static @Nullable AmecsKeyModifier fromKeyCode(int keyCode) {
		requireSealed();

		for (AmecsKeyModifier keyModifier : MODIFIERS_BY_NAME.values()) {
			if (keyModifier.matches(keyCode)) {
				return keyModifier;
			}
		}
		return null;
	}

	public static @Nullable AmecsKeyModifier fromKey(InputConstants.Key key) {
		requireSealed();

		if (
			key == null
				//# if MC_VERSION_NUMBER >= 260300
				|| key.getType() != InputConstants.Type.KEYBOARD
				//# else
				//- || key.getType() != InputConstants.Type.KEYSYM
				//# end
		) {
			return null;
		}
		return fromKeyCode(key.getValue());
	}

	private static void requireUnsealed() {
		if (sealed) {
			throw new IllegalStateException("Cannot register new key modifiers after the API has been sealed");
		}
	}

	private static void requireSealed() {
		if (!sealed) {
			throw new IllegalStateException("Cannot access key modifiers before the API has been sealed");
		}
	}
}
