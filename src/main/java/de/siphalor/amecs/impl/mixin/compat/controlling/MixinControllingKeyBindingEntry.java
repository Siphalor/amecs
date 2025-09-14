package de.siphalor.amecs.impl.mixin.compat.controlling;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.blamejared.controlling.client.NewKeyBindsList$KeyEntry", remap = false)
public abstract class MixinControllingKeyBindingEntry {
	@Inject(method = "lambda$new$0", at = @At("HEAD"))
	public void onEditButtonClicked(KeyMapping keyBinding, Button button, CallbackInfo callbackInfo) {
		keyBinding.setKey(InputConstants.UNKNOWN);
	}
}
