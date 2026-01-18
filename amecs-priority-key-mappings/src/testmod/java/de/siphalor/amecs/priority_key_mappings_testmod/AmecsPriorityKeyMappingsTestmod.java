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
