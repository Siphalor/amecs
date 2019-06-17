package de.siphalor.amecs.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.controls.ControlsOptionsScreen;
import net.minecraft.util.SystemUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
	@Inject(method = "onKey", at = @At("HEAD"))
	private void onKey(long window, int int_1, int int_2, int int_3, int int_4, CallbackInfo callbackInfo) {
		// Key released
		if(int_3 == 0 && MinecraftClient.getInstance().currentScreen instanceof ControlsOptionsScreen) {
			ControlsOptionsScreen screen = (ControlsOptionsScreen) MinecraftClient.getInstance().currentScreen;

			screen.focusedBinding = null;
            screen.time = SystemUtil.getMeasuringTimeMs();
		}
	}
}
