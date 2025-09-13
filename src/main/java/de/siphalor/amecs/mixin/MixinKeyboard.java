package de.siphalor.amecs.mixin;

import de.siphalor.amecs.Amecs;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(KeyboardHandler.class)
public class MixinKeyboard {
	//# if MC_VERSION_NUMBER >= 12109
	@ModifyVariable(
			method = "keyPress",
			argsOnly = true,
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyboardHandler;debugCrashKeyTime:J", ordinal = 0)
	)
	public KeyEvent modifyPressedKey(KeyEvent keyEvent) {
		if (Amecs.ESCAPE_KEYBINDING.matches(keyEvent)) {
			return new KeyEvent(GLFW.GLFW_KEY_ESCAPE, -1, keyEvent.modifiers());
		}
		return keyEvent;
	}
	//# else
	//- @ModifyVariable(
	//- 		method = "keyPress",
	//- 		argsOnly = true,
	//- 		ordinal = 0,
	//- 		at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyboardHandler;debugCrashKeyTime:J", ordinal = 0)
	//- )
	//- public int modifyPressedKey(int key, long window, int key_, int scancode) {
	//- 	if (Amecs.ESCAPE_KEYBINDING.matches(key, scancode)) {
	//- 		return GLFW.GLFW_KEY_ESCAPE;
	//- 	}
	//- 	return key;
	//- }
	//# end
}
