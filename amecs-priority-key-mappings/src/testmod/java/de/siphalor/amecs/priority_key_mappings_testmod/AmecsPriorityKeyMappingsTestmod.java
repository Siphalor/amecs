package de.siphalor.amecs.priority_key_mappings_testmod;

import de.siphalor.amecs.priority_key_mappings.api.AmecsPriorityKeyMapping;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class AmecsPriorityKeyMappingsTestmod implements ClientModInitializer {
	public static final String MOD_ID = "amecs_priority_key_mappings_testmod";

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(new TestKeyBinding(
				MOD_ID + ".test",
				GLFW.GLFW_KEY_U,
				KeyMapping.Category.MISC
		));
	}

	private static class TestKeyBinding extends KeyMapping implements AmecsPriorityKeyMapping {
		public TestKeyBinding(String string, int key, Category category) {
			super(string, key, category);
		}

		@Override
		public boolean onPressedPriority() {
			System.out.println("Pressed!");
			return true;
		}
	}
}
