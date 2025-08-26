/*
 * Copyright 2020-2023 Siphalor
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

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.impl.duck.IKeyBinding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

/**
 * A {@link net.minecraft.client.KeyMapping} base class to be used when you want to define modifiers keys as default
 */
@Environment(EnvType.CLIENT)
public class AmecsKeyBinding extends KeyMapping {
	private final KeyModifiers defaultModifiers;

	/**
	 * Constructs a new amecs keybinding. And because how the vanilla key binding works. It is automatically registered.
	 * <br>
	 * See {@link KeyBindingUtils#unregisterKeyBinding(KeyMapping)} for how to unregister it
	 * If you want to set the key's translationKey directly use {@link #AmecsKeyBinding(String, InputConstants.Type, int, String, KeyModifiers)} instead
	 *
	 * @param id the id to use
	 * @param type the input type which triggers this keybinding
	 * @param code the default key code
	 * @param category the id of the category which should include this keybinding
	 * @param defaultModifiers the default modifiers
	 */
	public AmecsKeyBinding(ResourceLocation id, InputConstants.Type type, int code, String category, KeyModifiers defaultModifiers) {
		this("key." + id.getNamespace() + "." + id.getPath(), type, code, category, defaultModifiers);
	}

	/**
	 * Constructs a new amecs keybinding. And because how the vanilla key binding works. It is automatically registered.
	 * <br>
	 * See {@link KeyBindingUtils#unregisterKeyBinding(KeyMapping)} for how to unregister it
	 *
	 * @param id the id to use
	 * @param type the input type which triggers this keybinding
	 * @param code the default key code
	 * @param category the id of the category which should include this keybinding
	 * @param defaultModifiers the default modifiers
	 */
	public AmecsKeyBinding(String id, InputConstants.Type type, int code, String category, KeyModifiers defaultModifiers) {
		super(id, type, code, category);
		if (defaultModifiers == null || defaultModifiers == KeyModifiers.NO_MODIFIERS) {
			defaultModifiers = new KeyModifiers(); // the modifiable version of: KeyModifiers.NO_MODIFIERS
		}
		this.defaultModifiers = defaultModifiers;
		((IKeyBinding) this).amecs$getKeyModifiers().copyModifiers(this.defaultModifiers);
	}

	@Override
	public void setDown(boolean pressed) {
		super.setDown(pressed);
		if (pressed) {
			onPressed();
		} else {
			onReleased();
		}
	}

	/**
	 * A convenience method which gets fired when the keybinding is used
	 */
	public void onPressed() {
	}

	/**
	 * A convenience method which gets fired when the keybinding is stopped being used
	 */
	public void onReleased() {
	}

	/**
	 * Resets this keybinding (triggered when the user clicks on the "Reset" button).
	 */
	public void resetKeyBinding() {
		((IKeyBinding) this).amecs$getKeyModifiers().copyModifiers(defaultModifiers);
	}

	@Override
	public boolean isDefault() {
		return defaultModifiers.equals(((IKeyBinding) this).amecs$getKeyModifiers()) && super.isDefault();
	}

	public KeyModifiers getDefaultModifiers() {
		return defaultModifiers;
	}
}
