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

package de.siphalor.amecs.key_modifiers.api;

import de.siphalor.amecs.key_modifiers.impl.duck.IKeyMapping;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
//- import net.minecraft.resources.ResourceLocation;

/**
 * A {@link KeyMapping} base class to be used when you want to define modifiers keys as default
 */
@Environment(EnvType.CLIENT)
public class AmecsKeyMappingWithKeyModifiers extends KeyMapping {
	private final AmecsKeyModifierCombination defaultModifiers;

	/**
	 * Constructs a new amecs keybinding. And because how the vanilla key binding works. It is automatically registered.
	 * <br>
	 * See {@link KeyBindingUtils#unregisterKeyBinding(KeyMapping)} for how to unregister it
	 * If you want to set the key's translationKey directly use {@link #AmecsKeyMappingWithKeyModifiers(String, InputConstants.Type, int, Category, AmecsKeyModifierCombination)} instead
	 *
	 * @param id the id to use
	 * @param type the input type which triggers this keybinding
	 * @param code the default key code
	 * @param category the id of the category which should include this keybinding
	 * @param defaultModifiers the default modifiers
	 */
	public AmecsKeyMappingWithKeyModifiers(
			//# if MC_VERSION_NUMBER >= 260100
			Identifier id,
			//# else
			//- ResourceLocation id,
			//# end
			InputConstants.Type type,
			int code,
			//# if MC_VERSION_NUMBER >= 12109
			Category category,
			//# else
			//- String category,
			//# end
			AmecsKeyModifierCombination defaultModifiers
	) {
		this("key." + id.getNamespace() + "." + id.getPath(), type, code, category, defaultModifiers);
	}

	/**
	 * Constructs a new amecs keybinding. And because how the vanilla key binding works. It is automatically registered.
	 * <br>
	 * See {@link KeyBindingUtils#unregisterKeyBinding(KeyMapping)} for how to unregister it
	 *
	 * @param id the id/translation key to use
	 * @param type the input type which triggers this keybinding
	 * @param code the default key code
	 * @param category the id of the category which should include this keybinding
	 * @param defaultModifiers the default modifiers
	 */
	public AmecsKeyMappingWithKeyModifiers(
			String id,
			InputConstants.Type type,
			int code,
			//# if MC_VERSION_NUMBER >= 12109
			Category category,
			//# else
			//- String category,
			//# end
			AmecsKeyModifierCombination defaultModifiers
	) {
		super(id, type, code, category);
		if (defaultModifiers == null || defaultModifiers == AmecsKeyModifierCombination.NO_MODIFIERS) {
			defaultModifiers = new AmecsKeyModifierCombination(); // the modifiable version of: KeyModifiers.NO_MODIFIERS
		}
		this.defaultModifiers = defaultModifiers;
		((IKeyMapping) this).amecs$getBoundKeyModifiers().copyFrom(this.defaultModifiers);
	}

	//# if MC_VERSION_NUMBER >= 11500
	@Override
	//# end
	public void setDown(boolean pressed) {
		//# if MC_VERSION_NUMBER >= 11500
		super.setDown(pressed);
		//# else
		//- ((IKeyMapping) this).amecs$setDown(pressed);
		//# end
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
		((IKeyMapping) this).amecs$getBoundKeyModifiers().copyFrom(defaultModifiers);
	}

	@Override
	public boolean isDefault() {
		return defaultModifiers.equals(((IKeyMapping) this).amecs$getBoundKeyModifiers()) && super.isDefault();
	}

	public AmecsKeyModifierCombination getDefaultAmecsKeyModifiers() {
		return defaultModifiers;
	}
}
