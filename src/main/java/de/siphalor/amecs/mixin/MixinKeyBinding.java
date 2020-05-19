package de.siphalor.amecs.mixin;

import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.KeyBindingManager;
import de.siphalor.amecs.util.IKeyBinding;
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

import java.util.Map;

@SuppressWarnings("WeakerAccess")
@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements IKeyBinding {
	@Shadow private InputUtil.KeyCode keyCode;

	@Shadow private int timesPressed;

	@Shadow private boolean pressed;

	@Shadow @Final private static Map<InputUtil.KeyCode, KeyBinding> keysByCode;

	@Shadow @Final private static Map<String, KeyBinding> keysById;

	@Shadow protected abstract void reset();

	private final KeyModifiers amecs$keyModifiers = new KeyModifiers();

	@Override
	public InputUtil.KeyCode amecs$getKeyCode() {
		return keyCode;
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
	public KeyModifiers amecs$getKeyModifiers() {
		return amecs$keyModifiers;
	}

	@Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("RETURN"))
	private void onConstructed(String id, InputUtil.Type type, int defaultCode, String category, CallbackInfo callbackInfo) {
		keysByCode.remove(keyCode);
		KeyBindingManager.register((KeyBinding)(Object) this);
	}

	@Inject(method = "getLocalizedName()Ljava/lang/String;", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	public void getLocalizedName(CallbackInfoReturnable<String> callbackInfoReturnable, String i18nName, String glfwName) {
		 StringBuilder extra = new StringBuilder();
		 if(amecs$keyModifiers.getShift()) extra.append("Shift + ");
		 if(amecs$keyModifiers.getControl()) extra.append("Control + ");
		 if(amecs$keyModifiers.getAlt()) extra.append("Alt + ");
		callbackInfoReturnable.setReturnValue(extra.toString() + (glfwName == null ? I18n.translate(i18nName) : glfwName));
	}

	@Inject(method = "matchesKey", at = @At("RETURN"), cancellable = true)
	public void matchesKey(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if(!amecs$keyModifiers.isUnset() && !amecs$keyModifiers.match()) callbackInfoReturnable.setReturnValue(false);
	}

	@Inject(method = "matchesMouse", at = @At("RETURN"), cancellable = true)
	public void matchesMouse(int mouse, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if(!amecs$keyModifiers.isUnset() && !amecs$keyModifiers.match()) callbackInfoReturnable.setReturnValue(false);
	}

	@Inject(method = "equals", at = @At("RETURN"), cancellable = true)
	public void equals(KeyBinding other, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if(!amecs$keyModifiers.equals(((IKeyBinding) other).amecs$getKeyModifiers())) callbackInfoReturnable.setReturnValue(false);
	}

	@Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
	private static void onKeyPressed(InputUtil.KeyCode keyCode, CallbackInfo callbackInfo) {
		KeyBindingManager.onKeyPressed(keyCode);
		callbackInfo.cancel();
	}

	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void setKeyPressed(InputUtil.KeyCode keyCode, boolean pressed, CallbackInfo callbackInfo) {
		KeyBindingManager.setKeyPressed(keyCode, pressed);
	}

	@Inject(method = "updatePressedStates", at = @At("HEAD"), cancellable = true)
	private static void updatePressedStates(CallbackInfo callbackInfo) {
		KeyBindingManager.updatePressedStates();
		callbackInfo.cancel();
	}

	@Inject(method = "updateKeysByCode", at = @At("HEAD"), cancellable = true)
	private static void updateKeyBindings(CallbackInfo callbackInfo) {
		KeyBindingManager.keysById.clear();
        keysById.values().forEach(KeyBindingManager::register);
		callbackInfo.cancel();
	}

	@Inject(method = "unpressAll", at = @At("HEAD"), cancellable = true)
	private static void unpressAll(CallbackInfo callbackInfo) {
		KeyBindingManager.unpressAll();
		callbackInfo.cancel();
	}

	@SuppressWarnings("unused")
	private static Map<String, KeyBinding> amecs$getIdToKeyBindingMap() {
		return keysById;
	}
}
