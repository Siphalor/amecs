package de.siphalor.amecs.mixin;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.AmecsKeyBinding;
import de.siphalor.amecs.AmecsKeyBindingRegistry;
import de.siphalor.amecs.util.IKeyBinding;
import de.siphalor.amecs.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Mixin(KeyBinding.class)
public class MixinKeyBinding implements IKeyBinding {
	@Shadow private InputUtil.KeyCode keyCode;

	@Shadow private int timesPressed;

	@Shadow private boolean pressed;

	@Shadow @Final private static Map<InputUtil.KeyCode, KeyBinding> keysByCode;

	@Shadow @Final private static Map<String, KeyBinding> keysById;
	private AmecsKeyBinding amecs$amecsKeyBinding;

	public InputUtil.KeyCode getKeyCode() {
		return keyCode;
	}

	@Override
	public AmecsKeyBinding amecs$getAmecsKeyBinding() {
		return amecs$amecsKeyBinding;
	}

	@Override
	public void amecs$setAmecsKeyBinding(AmecsKeyBinding amecsKeyBinding) {
		amecs$amecsKeyBinding = amecsKeyBinding;
	}

	@Override
	public int amecs$getTimesPressed() {
		return timesPressed;
	}

	@Override
	public void amecs$setTimesPressed(int timesPressed) {
        this.timesPressed = timesPressed;
	}

	@Override
	public void amecs$setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("RETURN"))
	private void onConstructed(String id, InputUtil.Type type, int defaultCode, String category, CallbackInfo callbackInfo) {
		keysByCode.remove(keyCode);
        AmecsKeyBindingRegistry.register(keyCode, new AmecsKeyBinding((KeyBinding)(Object) this));
	}

	@Inject(method = "getLocalizedName()Ljava/lang/String;", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	public void getLocalizedName(CallbackInfoReturnable<String> callbackInfoReturnable, String i18nName, String glfwName) {
		if(amecs$amecsKeyBinding != null) {
			StringBuilder extra = new StringBuilder();
			if(amecs$amecsKeyBinding.getShift()) extra.append("Shift + ");
			if(amecs$amecsKeyBinding.getControl()) extra.append("Control + ");
			if(amecs$amecsKeyBinding.getAlt()) extra.append("Alt + ");
            callbackInfoReturnable.setReturnValue(extra.toString() + (glfwName == null ? I18n.translate(i18nName) : glfwName));
		}
	}

	@Inject(method = "matchesKey", at = @At("RETURN"), cancellable = true)
	public void matchesKey(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if(amecs$amecsKeyBinding != null && !amecs$amecsKeyBinding.modifiersMatch()) callbackInfoReturnable.setReturnValue(false);
	}

	@Inject(method = "matchesMouse", at = @At("RETURN"), cancellable = true)
	public void matchesMouse(int mouse, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if(amecs$amecsKeyBinding != null && !amecs$amecsKeyBinding.modifiersMatch()) callbackInfoReturnable.setReturnValue(false);
	}

	@Inject(method = "equals", at = @At("RETURN"), cancellable = true)
	public void equals(KeyBinding other, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if(!Objects.equals(amecs$amecsKeyBinding, ((IKeyBinding) other).amecs$getAmecsKeyBinding())) callbackInfoReturnable.setReturnValue(false);
	}

	@Inject(method = "onKeyPressed", at = @At("HEAD"))
	private static void onKeyPressed(InputUtil.KeyCode keyCode, CallbackInfo callbackInfo) {
		Collection<AmecsKeyBinding> amecsKeyBindings = AmecsKeyBindingRegistry.codeToBindings.get(keyCode);
		if(amecsKeyBindings == null) return;
		AmecsKeyBindingRegistry.getMatchingKeyBindings(keyCode).forEach(amecsKeyBinding ->
				((IKeyBinding) amecsKeyBinding.getKeyBinding()).amecs$setTimesPressed(((IKeyBinding) amecsKeyBinding.getKeyBinding()).amecs$getTimesPressed() + 1)
			);
	}

	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void setKeyPressed(InputUtil.KeyCode keyCode, boolean pressed, CallbackInfo callbackInfo) {
		if (Amecs.isShiftKey(keyCode.getKeyCode())) {
			AmecsKeyBinding.currentModifiers = Utils.setFlag(AmecsKeyBinding.currentModifiers, AmecsKeyBinding.SHIFT, pressed);
		} else if(Amecs.isControlKey(keyCode.getKeyCode())) {
			AmecsKeyBinding.currentModifiers = Utils.setFlag(AmecsKeyBinding.currentModifiers, AmecsKeyBinding.CONTROL, pressed);
		} else if(Amecs.isAltKey(keyCode.getKeyCode())) {
			AmecsKeyBinding.currentModifiers = Utils.setFlag(AmecsKeyBinding.currentModifiers, AmecsKeyBinding.ALT, pressed);
		}

		Collection<AmecsKeyBinding> amecsKeyBindings = AmecsKeyBindingRegistry.codeToBindings.get(keyCode);
		if(amecsKeyBindings == null) return;
		AmecsKeyBindingRegistry.getMatchingKeyBindings(keyCode).forEach(amecsKeyBinding -> ((IKeyBinding) amecsKeyBinding.getKeyBinding()).amecs$setPressed(pressed));
	}

	@Inject(method = "updatePressedStates", at = @At("HEAD"), cancellable = true)
	private static void updatePressedStates(CallbackInfo callbackInfo) {
		Collection<AmecsKeyBinding> amecsKeyBindings = AmecsKeyBindingRegistry.codeToBindings.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
		for(AmecsKeyBinding amecsKeyBinding : amecsKeyBindings) {
			boolean pressed = !amecsKeyBinding.getKeyBinding().isNotBound() && ((IKeyBinding) amecsKeyBinding.getKeyBinding()).getKeyCode().getCategory() == InputUtil.Type.KEYSYM && InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), ((IKeyBinding) amecsKeyBinding.getKeyBinding()).getKeyCode().getKeyCode());
			((IKeyBinding) amecsKeyBinding.getKeyBinding()).amecs$setPressed(pressed);
		}
		callbackInfo.cancel();
	}

	@Inject(method = "updateKeysByCode", at = @At("HEAD"), cancellable = true)
	private static void updateKeyBindings(CallbackInfo callbackInfo) {
		AmecsKeyBindingRegistry.update();
		callbackInfo.cancel();
	}

	@Inject(method = "unpressAll", at = @At("HEAD"), cancellable = true)
	private static void unpressAll(CallbackInfo callbackInfo) {
		AmecsKeyBindingRegistry.codeToBindings.values().stream().flatMap(Collection::stream).forEach(amecsKeyBinding ->
				((IKeyBinding) amecsKeyBinding.getKeyBinding()).amecs$setPressed(false)
			);
		callbackInfo.cancel();
	}

	private static Map<String, KeyBinding> amecs$getIdToKeyBindingMap() {
		return keysById;
	}
}
