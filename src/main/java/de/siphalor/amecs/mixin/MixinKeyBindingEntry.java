package de.siphalor.amecs.mixin;

import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.util.IKeyBinding;
import de.siphalor.amecs.util.IKeyBindingEntry;
import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("WeakerAccess")
@Mixin(ControlsListWidget.KeyBindingEntry.class)
public class MixinKeyBindingEntry implements IKeyBindingEntry {
	@Shadow @Final private KeyBinding binding;

	@Shadow @Final private String bindingName;

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_19870(Lnet/minecraft/client/options/KeyBinding;Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("RETURN"))
	public void onResetButtonClicked(KeyBinding keyBinding, ButtonWidget buttonWidget, CallbackInfo callbackInfo) {
		((IKeyBinding) binding).amecs$getKeyModifiers().unset();
		if(binding instanceof AmecsKeyBinding) ((AmecsKeyBinding) binding).resetKeyBinding();
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_19871(Lnet/minecraft/client/options/KeyBinding;Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At("RETURN"))
	public void onEditButtonClicked(KeyBinding keyBinding, ButtonWidget buttonWidget, CallbackInfo callbackInfo) {
		((IKeyBinding) binding).amecs$getKeyModifiers().unset();
        binding.setKeyCode(InputUtil.UNKNOWN_KEYCODE);
	}

	@Override
	public String amecs$getBindingName() {
		return bindingName;
	}

	@Override
	public KeyBinding amecs$getKeyBinding() {
		return binding;
	}
}
