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

package de.siphalor.amecs.keybinding;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyMappingWithKeyModifiers;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ToggleAutoJumpKeyMapping extends AmecsKeyMappingWithKeyModifiers {
	public ToggleAutoJumpKeyMapping(
			ResourceLocation id,
			InputConstants.Type type,
			int code,
			//# if MC_VERSION_NUMBER >= 12109
			Category category,
			//# else
			//- String category,
			//# end
			AmecsKeyModifierCombination defaultModifiers
	) {
		super(id, type, code, category, defaultModifiers);
	}

	@Override
	public void onPressed() {
		Minecraft minecraft = Minecraft.getInstance();
		//# if MC_VERSION_NUMBER >= 11900
		boolean autoJump = !minecraft.options.autoJump().get();
		minecraft.options.autoJump().set(autoJump);
		Amecs.sendToggleMessage(minecraft.player, autoJump, Component.translatable("amecs.toggled.auto_jump"));
		//# else
		//- boolean autoJump = !minecraft.options.autoJump;
		//- minecraft.options.autoJump = autoJump;
		//- Amecs.sendToggleMessage(minecraft.player, autoJump, new TranslatableComponent("amecs.toggled.auto_jump"));
		//# end
	}
}
