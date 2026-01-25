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

import de.siphalor.amecs.impl.AmecsLegacyDeprecationEmitter;
import de.siphalor.amecs.impl.OldKeyModifiersView;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiersApi;
import de.siphalor.amecs.key_modifiers.impl.AmecsKeyMappingManager;
import de.siphalor.amecs.key_modifiers.impl.AmecsKeyModifiersModule;
import java.util.Map;
import lombok.CustomLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

/**
 * Utility methods and constants for Amecs and vanilla key bindings
 * @deprecated Use {@link AmecsKeyModifiersApi} instead
 */
@SuppressWarnings({"unused", "removal"})
@Environment(EnvType.CLIENT)
@CustomLog
@Deprecated(forRemoval = true)
public class KeyBindingUtils {
	public static final int MOUSE_SCROLL_UP = 512;
	public static final int MOUSE_SCROLL_DOWN = 513;
	//# if MC_VERSION_NUMBER >= 12002
	public static final int MOUSE_SCROLL_LEFT = 514;
	public static final int MOUSE_SCROLL_RIGHT = 515;
	//# end

	/**
	 * The last (y directional) scroll delta
	 */
	@Deprecated
	private static double lastScrollAmountY = 0;
	private static Map<String, KeyMapping> idToKeyBindingMap;

	private KeyBindingUtils() {
	}

	/**
	 * Gets the last (y directional) scroll amount.
	 *
	 * @return the last (y directional) scroll amount
	 * @deprecated Just exists for legacy support.
	 */
	@Deprecated
	public static double getLastScrollAmount() {
		AmecsLegacyDeprecationEmitter.invoke();
		return lastScrollAmountY;
	}

	/**
	 * Sets the last (y directional) scroll amount. <b>For internal use only.</b>
	 *
	 * @param lastScrollAmountY the amount
	 * @deprecated Just exists for legacy support.
	 */
	@Deprecated
	public static void setLastScrollAmount(double lastScrollAmountY) {
		AmecsLegacyDeprecationEmitter.invoke();
		KeyBindingUtils.lastScrollAmountY = lastScrollAmountY;
	}

	/**
	 * Gets the key object for the vertical scroll direction
	 *
	 * @param deltaY the vertical (y) scroll amount {@link #getLastScrollAmount}
	 * @return the key object
	 * @see #getKeyFromVerticalScroll(double)
	 * @see #getKeyFromHorizontalScroll(double)
	 * @deprecated Just exists for legacy support. Use {@link #getKeyFromVerticalScroll(double)} instead.
	 */
	@Deprecated
	public static InputConstants.Key getKeyFromScroll(double deltaY) {
		AmecsLegacyDeprecationEmitter.invoke();
		return InputConstants.Type.MOUSE.getOrCreate(deltaY > 0 ? KeyBindingUtils.MOUSE_SCROLL_UP : KeyBindingUtils.MOUSE_SCROLL_DOWN);
	}

	/**
	 * Gets the key object for the vertical scroll direction
	 *
	 * @param deltaY the vertical (y) scroll amount {@link #getLastScrollAmount}
	 * @return the key object
	 * @deprecated Currently no replacement exists, please reach out if you are interested.
	 */
	public static InputConstants.Key getKeyFromVerticalScroll(double deltaY) {
		AmecsLegacyDeprecationEmitter.invoke();
		if (deltaY == 0D) {
			return null;
		}
		return InputConstants.Type.MOUSE.getOrCreate(deltaY > 0 ? KeyBindingUtils.MOUSE_SCROLL_UP : KeyBindingUtils.MOUSE_SCROLL_DOWN);
	}

	//# if MC_VERSION_NUMBER >= 12002
	/**
	 * Gets the key object for the horizontal scroll direction
	 *
	 * @param deltaX the horizontal (x) scroll amount {@link #getLastScrollAmount}
	 * @return the key object
	 * @deprecated Currently no replacement exists, please reach out if you are interested.
	 */
	public static InputConstants.Key getKeyFromHorizontalScroll(double deltaX) {
		AmecsLegacyDeprecationEmitter.invoke();
		if (deltaX == 0D) {
			return null;
		}
		return InputConstants.Type.MOUSE.getOrCreate(deltaX > 0 ? KeyBindingUtils.MOUSE_SCROLL_RIGHT : KeyBindingUtils.MOUSE_SCROLL_LEFT);
	}
	//# end

	/**
	 * Gets the "official" idToKeys map
	 *
	 * @return the map (use with care)
	 * @deprecated Currently no replacement exists, please reach out if you are interested.
	 */
	public static Map<String, KeyMapping> getIdToKeyBindingMap() {
		AmecsLegacyDeprecationEmitter.invoke();
		return AmecsKeyModifiersModule.getIdToKeyBindingMap();
	}

	/**
	 * Unregisters a keybinding from input querying but is NOT removed from the controls GUI
	 * <br>
	 * if you unregister a keybinding which is already in the controls GUI you can call {@link #registerHiddenKeyBinding(KeyMapping)} with this keybinding to undo this
	 * <br>
	 * <br>
	 * This is possible even after the game initialized
	 *
	 * @param keyBinding the keybinding
	 * @return whether the keyBinding was removed. It is not removed if it was not contained
	 * @deprecated Currently no replacement exists, please reach out if you are interested.
	 */
	public static boolean unregisterKeyBinding(KeyMapping keyBinding) {
		AmecsLegacyDeprecationEmitter.invoke();
		return unregisterKeyBinding(keyBinding.getName());
	}

	/**
	 * Unregisters a keybinding with the given id
	 * <br>
	 * for more details {@link #unregisterKeyBinding(KeyMapping)}
	 *
	 * @param id the translation key
	 * @return whether the keyBinding was removed. It is not removed if it was not contained
	 * @see #unregisterKeyBinding(KeyMapping)
	 * @deprecated Currently no replacement exists, please reach out if you are interested.
	 */
	public static boolean unregisterKeyBinding(String id) {
		AmecsLegacyDeprecationEmitter.invoke();
		KeyMapping keyBinding = getIdToKeyBindingMap().remove(id);
		return AmecsKeyMappingManager.unregister(keyBinding);
	}

	/**
	 * Registers a keybinding for input querying but is NOT added to the controls GUI
	 * <br>
	 * you can register a keybinding which is already in the controls GUI but was removed from input querying via {@link #unregisterKeyBinding(KeyMapping)}
	 * <br>
	 * <br>
	 * This is possible even after the game initialized
	 *
	 * @param keyBinding the keybinding
	 * @return whether the keybinding was added. It is not added if it is already contained
	 * @deprecated Currently no replacement exists, please reach out if you are interested.
	 */
	public static boolean registerHiddenKeyBinding(KeyMapping keyBinding) {
		AmecsLegacyDeprecationEmitter.invoke();
		return AmecsKeyMappingManager.register(keyBinding);
	}

	/**
	 * Gets the key modifiers that are bound to the given key binding
	 *
	 * @param keyBinding the key binding
	 * @return the key modifiers
	 * @deprecated Use {@link AmecsKeyModifiersApi#getBoundModifiers(KeyMapping)} instead
	 */
	public static KeyModifiers getBoundModifiers(KeyMapping keyBinding) {
		AmecsLegacyDeprecationEmitter.invoke();
		return new OldKeyModifiersView(AmecsKeyModifiersApi.getBoundModifiers(keyBinding));
	}

	/**
	 * Gets the default modifiers of the given key binding.
	 * The returned value <b>must not be modified!</b>
	 *
	 * @param keyBinding the key binding
	 * @return a reference to the default modifiers
	 * @deprecated Use {@link AmecsKeyModifiersApi#getDefaultModifiers(KeyMapping)} instead
	 */
	public static KeyModifiers getDefaultModifiers(KeyMapping keyBinding) {
		AmecsLegacyDeprecationEmitter.invoke();
		return new OldKeyModifiersView(AmecsKeyModifiersApi.getDefaultModifiers(keyBinding));
	}

	/**
	 * @deprecated Use {@link AmecsKeyModifiersApi#resetBoundModifiers(KeyMapping)} instead
	 */
	public static void resetBoundModifiers(KeyMapping keyBinding) {
		AmecsLegacyDeprecationEmitter.invoke();
		AmecsKeyModifiersApi.resetBoundModifiers(keyBinding);
	}
}
