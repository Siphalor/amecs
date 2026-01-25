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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;

public class SkinLayerKeyMapping extends AmecsKeyMappingWithKeyModifiers {
	private final PlayerModelPart playerModelPart;

	public SkinLayerKeyMapping(
			ResourceLocation id,
			InputConstants.Type type,
			int code,
			//# if MC_VERSION_NUMBER >= 12109
			Category category,
			//# else
			//- String category,
			//# end
			PlayerModelPart playerModelPart
	) {
		super(id, type, code, category, new AmecsKeyModifierCombination());
		this.playerModelPart = playerModelPart;
	}

	@Override
	public void onPressed() {
		Minecraft client = Minecraft.getInstance();
		//# if MC_VERSION_NUMBER >= 12102
		client.options.setModelPart(playerModelPart, !client.options.isModelPartEnabled(playerModelPart));
		Amecs.sendToggleMessage(client.player, client.options.isModelPartEnabled(playerModelPart), playerModelPart.getName());
		//# elif MC_VERSION_NUMBER >= 11700
		//- client.options.toggleModelPart(playerModelPart, !client.options.isModelPartEnabled(playerModelPart));
		//- Amecs.sendToggleMessage(client.player, client.options.isModelPartEnabled(playerModelPart), playerModelPart.getName());
		//# else
		//- client.options.toggleModelPart(playerModelPart);
		//- Amecs.sendToggleMessage(client.player, client.options.getModelParts().contains(playerModelPart), playerModelPart.getName());
		//# end
	}
}
