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

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.impl.OldKeyModifiersView;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyBindingWithKeyModifiers;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//- import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

/**
 * A {@link net.minecraft.client.KeyMapping} base class to be used when you want to define modifiers keys as default
 * @deprecated Use {@link AmecsKeyBindingWithKeyModifiers} instead
 */
@SuppressWarnings("removal")
@Environment(EnvType.CLIENT)
@Deprecated(forRemoval = true)
public class AmecsKeyBinding extends AmecsKeyBindingWithKeyModifiers {
	/**
	 * Constructs a new amecs keybinding. And because how the vanilla key binding works. It is automatically registered.
	 * <br>
	 * See {@link KeyBindingUtils#unregisterKeyBinding(KeyMapping)} for how to unregister it
	 * If you want to set the key's translationKey directly use {@link #AmecsKeyBinding(String, InputConstants.Type, int, Category, KeyModifiers)} instead
	 *
	 * @param id the id to use
	 * @param type the input type which triggers this keybinding
	 * @param code the default key code
	 * @param category the id of the category which should include this keybinding
	 * @param defaultModifiers the default modifiers
	 */
	public AmecsKeyBinding(
			ResourceLocation id,
			InputConstants.Type type,
			int code,
			//# if MC_VERSION_NUMBER >= 12109
			Category category,
			//# else
			//- String category,
			//# end
			KeyModifiers defaultModifiers
	) {
		super(id, type, code, category, mapModifiersToNew(defaultModifiers));
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
	public AmecsKeyBinding(
			String id,
			InputConstants.Type type,
			int code,
			//# if MC_VERSION_NUMBER >= 12109
			Category category,
			//# else
			//- String category,
			//# end
			KeyModifiers defaultModifiers
	) {
		super(id, type, code, category, mapModifiersToNew(defaultModifiers));
	}

	private static AmecsKeyModifierCombination mapModifiersToNew(KeyModifiers keyModifiers) {
		if (keyModifiers == null) {
			return new AmecsKeyModifierCombination();
		}
		return new AmecsKeyModifierCombination()
				.setAlt(keyModifiers.getAlt())
				.setControl(keyModifiers.getControl())
				.setShift(keyModifiers.getShift());
	}

	public KeyModifiers getDefaultModifiers() {
		return new OldKeyModifiersView(getDefaultAmecsKeyModifiers());
	}
}
