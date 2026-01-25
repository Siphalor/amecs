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

package de.siphalor.amecs.priority_key_mappings_testmod;

import de.siphalor.amecs.priority_key_mappings.api.AmecsPriorityKeyMapping;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;

public class AmecsPriorityKeyMappingsTestmod implements ClientModInitializer {
	public static final String MOD_ID = "amecs_priority_key_mappings_testmod";

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(new TestKeyBinding(
				MOD_ID + ".test",
				GLFW.GLFW_KEY_U,
				//# if MC_VERSION_NUMBER >= 12109
				KeyMapping.Category.MISC
				//# else
				//- "key.categories.misc"
				//# end
		));
	}

	private static class TestKeyBinding extends KeyMapping implements AmecsPriorityKeyMapping {
		public TestKeyBinding(
				String string,
				int key,
				//# if MC_VERSION_NUMBER >= 12109
				Category category
				//# else
				//- String category
				//# end
		) {
			super(string, key, category);
		}

		@Override
		public boolean onPressedPriority() {
			System.out.println("Pressed!");
			return true;
		}
	}
}
