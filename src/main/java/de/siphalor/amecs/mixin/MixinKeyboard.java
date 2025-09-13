package de.siphalor.amecs.mixin;

import de.siphalor.amecs.Amecs;
import net.minecraft.client.KeyboardHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(KeyboardHandler.class)
public class MixinKeyboard {
	@ModifyVariable(
			method = "keyPress",
			argsOnly = true,
			ordinal = 0,
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyboardHandler;debugCrashKeyTime:J")
	)
	public int modifyPressedKey(int key, long window, int key_, int scancode) {
		if (Amecs.ESCAPE_KEYBINDING.matches(key, scancode)) {
			return GLFW.GLFW_KEY_ESCAPE;
		}
		return key;
	}
}
