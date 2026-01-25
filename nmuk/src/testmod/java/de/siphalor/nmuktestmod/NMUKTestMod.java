/*
 * Copyright 2021 Siphalor
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

package de.siphalor.nmuktestmod;

import de.siphalor.amecs.key_modifiers.api.AmecsKeyMappingWithKeyModifiers;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
import de.siphalor.nmuk.api.NMUKAlternatives;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

public class NMUKTestMod implements ModInitializer {
	public static final String MOD_ID = "nmuk_testmod";

	@Override
	public void onInitialize() {
		KeyMapping kbd = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				MOD_ID + ".test",
				InputConstants.Type.KEYSYM,
				86,
				//# if MC_VERSION_NUMBER >= 12109
				KeyMapping.Category.MOVEMENT
				//# else
				//- "key.categories.movement"
				//# end
		));
		NMUKAlternatives.create(kbd, 85);
		NMUKAlternatives.create(kbd, new AmecsKeyMappingWithKeyModifiers(
				//# if MC_VERSION_NUMBER >= 12100
				ResourceLocation.fromNamespaceAndPath(MOD_ID, "alt"),
				//# else
				//- new ResourceLocation(MOD_ID, "alt"),
				//# end
				InputConstants.Type.KEYSYM,
				86,
				//# if MC_VERSION_NUMBER >= 12109
				KeyMapping.Category.MOVEMENT,
				//# else
				//- "key.categories.movement",
				//# end
				new AmecsKeyModifierCombination(false, true, true)
		));
	}
}
