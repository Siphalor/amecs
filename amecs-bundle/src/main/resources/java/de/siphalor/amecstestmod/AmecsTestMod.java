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

package de.siphalor.amecstestmod;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.nmuk.api.NMUKAlternatives;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

public class AmecsTestMod implements ClientModInitializer {
	public static final String MOD_ID = "amecstestmod";

	@Override
	public void onInitializeClient() {
		KeyMapping kbd = KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(
				//# if MC_VERSION_NUMBER >= 12100
				ResourceLocation.fromNamespaceAndPath(MOD_ID, "kbd"),
				//# else
				//- new ResourceLocation(MOD_ID, "kbd"),
				//# end
				InputConstants.Type.KEYSYM,
				86,
				//# if MC_VERSION_NUMBER >= 12109
				KeyMapping.Category.MOVEMENT,
				//# else
				//- "key.categories.movement",
				//# end
				new KeyModifiers(false, false, false)
		));
		NMUKAlternatives.create(kbd, new AmecsKeyBinding(
				//# if MC_VERSION_NUMBER >= 12100
				ResourceLocation.fromNamespaceAndPath(MOD_ID, "test"),
				//# else
				//- new ResourceLocation(MOD_ID, "test"),
				//# end
				InputConstants.Type.KEYSYM,
				87,
				//# if MC_VERSION_NUMBER >= 12109
				KeyMapping.Category.MISC,
				//# else
				//- "",
				//# end
				new KeyModifiers(true, false, false)
		));
	}
}
