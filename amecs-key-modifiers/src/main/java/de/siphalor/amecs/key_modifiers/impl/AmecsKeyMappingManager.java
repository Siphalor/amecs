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

package de.siphalor.amecs.key_modifiers.impl;

import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifier;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiersApi;
import de.siphalor.amecs.key_modifiers.impl.duck.IKeyMapping;
import java.util.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public class AmecsKeyMappingManager {
	private static final List<AmecsKeyMappingManagerLayer> LAYERS = new ArrayList<>();
	private static final List<KeyMapping> PRESSED_MAPPINGS = new ArrayList<>(10);

	static {
		LAYERS.add(new AmecsKeyMappingManagerLayer());
	}

	private AmecsKeyMappingManager() {}

	public static void prependLayer(AmecsKeyMappingManagerLayer layer) {
		//noinspection SequencedCollectionMethodCanBeUsed
		LAYERS.add(0, layer);
	}

	/**
	 * Registers a key binding to Amecs API
	 * @param mapping the key binding to register
	 * @return whether the keyBinding was added. It is not added if it is already contained
	 */
	public static boolean register(KeyMapping mapping) {
		for (AmecsKeyMappingManagerLayer layer : LAYERS) {
			if (layer.accepts(mapping)) {
				return layer.register(mapping);
			}
		}
		return false;
	}

	public static void onKeyPressed(InputConstants.Key input) {
		for (AmecsKeyMappingManagerLayer layer : LAYERS) {
			layer.getMappingsForInput(input).forEach(keyBinding ->
					((IKeyMapping) keyBinding).amecs$incrementTimesPressed()
			);
		}
	}

	public static void updatePressedStates() {
		//# if MC_VERSION_NUMBER >= 12109
		Window windowHandle = Minecraft.getInstance().getWindow();
		//# elif MC_VERSION_NUMBER >= 11500
		//- long windowHandle = Minecraft.getInstance().getWindow().getWindow();
		//# else
		//- long windowHandle = Minecraft.getInstance().window.getWindow();
		//# end
		for (AmecsKeyMappingManagerLayer layer : LAYERS) {
			layer.getAllMappings().forEach(keyBinding -> {
				InputConstants.Key key = ((IKeyMapping) keyBinding).amecs$getBoundKey();
				boolean pressed = !keyBinding.isUnbound() && key.getType() == InputConstants.Type.KEYSYM && InputConstants.isKeyDown(windowHandle, key.getValue());
				setKeyBindingPressed(keyBinding, pressed);
			});
		}
	}

	/**
	 * Unregisters a key binding from Amecs API
	 * @param keyBinding the key binding to unregister
	 * @return whether the keyBinding was removed. It is not removed if it was not contained
	 */
	public static boolean unregister(KeyMapping keyBinding) {
		if (keyBinding == null) {
			return false;
		}
		// avoid having to rebuild the whole entry map with KeyMapping.updateKeysByCode()
		boolean removed = false;
		for (AmecsKeyMappingManagerLayer layer : LAYERS) {
			removed |= layer.unregister(keyBinding);
		}
		return removed;
	}

	public static void updateKeysByCode() {
		LAYERS.forEach(AmecsKeyMappingManagerLayer::clear);
		AmecsKeyModifiersModule.getIdToKeyBindingMap().values().forEach(AmecsKeyMappingManager::register);
	}

	public static void setKeyBindingPressed(KeyMapping keyBinding, boolean pressed) {
		if (pressed != keyBinding.isDown()) {
			if (pressed) {
				PRESSED_MAPPINGS.add(keyBinding);
			} else {
				PRESSED_MAPPINGS.remove(keyBinding);
			}
		}
		//# if MC_VERSION_NUMBER >= 11500
		keyBinding.setDown(pressed);
		//# else
		//- ((IKeyMapping) keyBinding).amecs$setDown(pressed);
		//# end
	}

	public static void unpressAll() {
		AmecsKeyModifiersModule.getIdToKeyBindingMap().values().forEach(keyBinding -> ((IKeyMapping) keyBinding).amecs$reset());
	}

	public static void setKeyPressed(InputConstants.Key keyCode, boolean pressed) {
		AmecsKeyModifier modifier = AmecsKeyModifiers.fromKeyCode(keyCode.getValue());
		if (modifier != null) {
			AmecsKeyModifiersModule.CURRENT_MODIFIERS.set(modifier, pressed);
		}

		// Update keybindings with matching modifiers and the same keycode
		for (AmecsKeyMappingManagerLayer layer : LAYERS) {
			layer.getMappingsForInput(keyCode).forEach(keyBinding -> setKeyBindingPressed(keyBinding, pressed));
		}

		if (modifier != null && !pressed) {
			handleReleasedModifier();
		}
	}

	private static void handleReleasedModifier() {
		// Handle the case that a modifier has been released
		PRESSED_MAPPINGS.removeIf(pressedKeyBinding -> {
			AmecsKeyModifierCombination boundModifiers = AmecsKeyModifiersApi.getBoundModifiers(pressedKeyBinding);
			if (!AmecsKeyModifiersModule.CURRENT_MODIFIERS.contains(boundModifiers)) {
				//# if MC_VERSION_NUMBER >= 11500
				pressedKeyBinding.setDown(false);
				//# else
				//- ((IKeyMapping) pressedKeyBinding).amecs$setDown(false);
				//# end
				return true;
			}
			return false;
		});
	}
}
