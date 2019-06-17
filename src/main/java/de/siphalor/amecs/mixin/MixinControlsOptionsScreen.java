package de.siphalor.amecs.mixin;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.AmecsKeyBinding;
import de.siphalor.amecs.util.IKeyBinding;
import net.minecraft.client.gui.screen.controls.ControlsOptionsScreen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.SystemUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("WeakerAccess")
@Mixin(ControlsOptionsScreen.class)
public class MixinControlsOptionsScreen {
    @Shadow public KeyBinding focusedBinding;

    @Shadow public long time;

    @Shadow @Final private GameOptions options;

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;setKeyCode(Lnet/minecraft/client/options/KeyBinding;Lnet/minecraft/client/util/InputUtil$KeyCode;)V", ordinal = 0))
    public void clearKeyBinding(int keyCode, int scanCode, int int_3, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        AmecsKeyBinding amecsKeyBinding = ((IKeyBinding) focusedBinding).amecs$getAmecsKeyBinding();
        if(amecsKeyBinding != null) {
            amecsKeyBinding.setAlt(false);
            amecsKeyBinding.setControl(false);
            amecsKeyBinding.setShift(false);
        }
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;setKeyCode(Lnet/minecraft/client/options/KeyBinding;Lnet/minecraft/client/util/InputUtil$KeyCode;)V", ordinal = 1), cancellable = true)
    public void onKeyPressed(int keyCode, int scanCode, int int_3, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(true);
    	if(focusedBinding.isNotBound()) {
            focusedBinding.setKeyCode(InputUtil.getKeyCode(keyCode, scanCode));
        } else {
            int mainKeyCode = ((IKeyBinding) focusedBinding).getKeyCode().getKeyCode();
            AmecsKeyBinding amecsKeyBinding = ((IKeyBinding) focusedBinding).amecs$getAmecsKeyBinding();
            if (amecsKeyBinding != null) {
                if (Amecs.isModifier(mainKeyCode) && !Amecs.isModifier(keyCode)) {
                    focusedBinding.setKeyCode(InputUtil.getKeyCode(keyCode, scanCode));
                    if (Amecs.isShiftKey(mainKeyCode)) amecsKeyBinding.setShift(true);
                    if (Amecs.isControlKey(mainKeyCode)) amecsKeyBinding.setControl(true);
                    if (Amecs.isAltKey(mainKeyCode)) amecsKeyBinding.setAlt(true);
                } else {
                    if (Amecs.isShiftKey(keyCode)) amecsKeyBinding.setShift(true);
                    if (Amecs.isControlKey(keyCode)) amecsKeyBinding.setControl(true);
                    if (Amecs.isAltKey(keyCode)) amecsKeyBinding.setAlt(true);
                    amecsKeyBinding.cleanupModifiers();
                }
            }
        }
        time = SystemUtil.getMeasuringTimeMs();
        KeyBinding.updateKeysByCode();
    }
}
