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

package de.siphalor.amecs.testmod;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

public class ClientInit implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(new TestPriorityKeybinding(
				//# if MC_VERSION_NUMBER >= 12100
				ResourceLocation.fromNamespaceAndPath("amecsapi-testmod", "priority"),
				//# else
				//- new ResourceLocation("amecsapi-testmod", "priority"),
				//# end
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_Z,
				//# if MC_VERSION_NUMBER >= 12109
				KeyMapping.Category.MISC,
				//# else
				//- "key.categories.misc",
				//# end
				new AmecsKeyModifierCombination(),
				() -> {
					System.out.println("priority");
					return true;
				},
				() -> {
					System.out.println("priority release");
					return true;
				}
		));
	}
}
