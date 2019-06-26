package de.siphalor.amecs.mixin;

import de.siphalor.amecs.api.KeyBindingUtils;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InputUtil.Type.class)
public abstract class MixinInputUtilType {
	@Shadow
	private static void mapKey(InputUtil.Type inputUtil$Type_1, String string_1, int int_1) {
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void onRegisterKeyCodes(CallbackInfo callbackInfo) {
		mapKey(InputUtil.Type.MOUSE, "amecs.key.mouse.scroll.up", KeyBindingUtils.MOUSE_SCROLL_UP);
		mapKey(InputUtil.Type.MOUSE, "amecs.key.mouse.scroll.down", KeyBindingUtils.MOUSE_SCROLL_DOWN);
	}
}
