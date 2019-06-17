package de.siphalor.amecs.mixin;

import de.siphalor.amecs.AmecsKeyBinding;
import de.siphalor.amecs.util.IKeyBinding;
import de.siphalor.amecs.util.IKeyBindingEntry;
import net.minecraft.client.gui.screen.controls.ControlsListWidget;
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
	@Inject(method = "method_19870", at = @At("RETURN"))
	public void onResetButtonClicked(KeyBinding keyBinding, ButtonWidget buttonWidget, CallbackInfo callbackInfo) {
		AmecsKeyBinding amecsKeyBinding = ((IKeyBinding) binding).amecs$getAmecsKeyBinding();
		if(amecsKeyBinding != null) {
			amecsKeyBinding.setAlt(false);
			amecsKeyBinding.setControl(false);
			amecsKeyBinding.setShift(false);
		}
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_19871", at = @At("RETURN"))
	public void onEditButtonClicked(KeyBinding keyBinding, ButtonWidget buttonWidget, CallbackInfo callbackInfo) {
		AmecsKeyBinding amecsKeyBinding = ((IKeyBinding) binding).amecs$getAmecsKeyBinding();
		if(amecsKeyBinding != null) {
			amecsKeyBinding.setControl(false);
			amecsKeyBinding.setAlt(false);
			amecsKeyBinding.setShift(false);
		}
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
