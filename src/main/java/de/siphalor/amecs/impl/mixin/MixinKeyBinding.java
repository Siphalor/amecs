/*
 * Copyright 2020-2023 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.amecs.impl.mixin;

import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
//- import net.minecraft.client.resources.language.I18n;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.KeyBindingManager;
import de.siphalor.amecs.impl.ModifierPrefixTextProvider;
import de.siphalor.amecs.impl.NOPMap;
import de.siphalor.amecs.impl.duck.IKeyBinding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//- import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("WeakerAccess")
@Environment(EnvType.CLIENT)
@Mixin(KeyMapping.class)
public abstract class MixinKeyBinding implements IKeyBinding {
	@Shadow
	private InputConstants.Key key;

	@Shadow
	private int clickCount;

	//# if MC_VERSION_NUMBER < 11500
	//- @Shadow
	//- private boolean isDown;
	//# end

	@Shadow
	@Final
	private static Map<String, KeyMapping> ALL;

	// set it to a NOPMap meaning everything done with this map is ignored. Because setting it to null would cause problems
	// ... even if we remove the put in the KeyMapping constructor. Because maybe in the future this map is used elsewhere or a other mod uses it
	@Shadow
	@Final
	@Mutable
	private static Map<InputConstants.Key, KeyMapping> MAP = NOPMap.nopMap();

	@Unique
	private final KeyModifiers keyModifiers = new KeyModifiers();

	@Override
	public InputConstants.Key amecs$getBoundKey() {
		return key;
	}

	@Override
	public int amecs$getTimesPressed() {
		return clickCount;
	}

	@Override
	public void amecs$setTimesPressed(int timesPressed) {
		this.clickCount = timesPressed;
	}

	@Override
	public void amecs$incrementTimesPressed() {
		clickCount++;
	}

	@Invoker("release")
	@Override
	public abstract void amecs$reset();

	@Override
	public KeyModifiers amecs$getKeyModifiers() {
		return keyModifiers;
	}

	//# if MC_VERSION_NUMBER < 11500
	//- @Override
	//- public void amecs$setDown(boolean pressed) {
	//- 	isDown = pressed;
	//- }
	//# end

	@Inject(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V", at = @At("RETURN"))
	private void onConstructed(String id, InputConstants.Type type, int defaultCode, String category, CallbackInfo callbackInfo) {
		KeyBindingManager.register((KeyMapping) (Object) this);
	}

	//# if MC_VERSION_NUMBER >= 11600
	@Inject(method = "getTranslatedKeyMessage", at = @At("TAIL"), cancellable = true)
	public void getLocalizedName(CallbackInfoReturnable<Component> callbackInfoReturnable) {
		Component name = key.getDisplayName();
		Component fullName;
		Font font = Minecraft.getInstance().font;
		ModifierPrefixTextProvider.Variation variation = ModifierPrefixTextProvider.Variation.WIDEST;
		do {
			fullName = name;
			for (KeyModifier keyModifier : KeyModifier.VALUES) {
				if (keyModifier == KeyModifier.NONE) {
					continue;
				}

				if (keyModifiers.get(keyModifier)) {
					fullName = keyModifier.textProvider.getComponent(variation).append(fullName);
				}
			}
		} while ((variation = variation.getSmaller()) != null && font.width(fullName) > 70);

		callbackInfoReturnable.setReturnValue(fullName);
	}
	//# else
	//- @Inject(method = "getTranslatedKeyMessage", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	//- public void getLocalizedName(CallbackInfoReturnable<String> callbackInfoReturnable, String i18nName, int keyCode, String glfwName) {
	//- 	String name = (glfwName == null ? I18n.get(i18nName) : glfwName);
	//- 	StringBuilder fullName;
	//- 	Font font = Minecraft.getInstance().font;
	//- 	ModifierPrefixTextProvider.Variation variation = ModifierPrefixTextProvider.Variation.WIDEST;
	//- 	do {
	//- 		fullName = new StringBuilder(name);
	//- 		for (KeyModifier keyModifier : KeyModifier.VALUES) {
	//- 			if (keyModifier == KeyModifier.NONE) {
	//- 				continue;
	//- 			}

	//- 			if (keyModifiers.get(keyModifier)) {
	//- 				fullName.insert(0, keyModifier.textProvider.getTranslation(variation));
	//- 			}
	//- 		}
	//- 	} while ((variation = variation.getSmaller()) != null && font.width(fullName.toString()) > 70);
	//- 	callbackInfoReturnable.setReturnValue(fullName.toString());
	//- }
	//# end

	@Inject(method = "matches", at = @At("RETURN"), cancellable = true)
	public void matchesKey(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (!keyModifiers.isUnset() && !keyModifiers.equals(KeyModifiers.getCurrentlyPressed())) {
			callbackInfoReturnable.setReturnValue(false);
		}
	}

	@Inject(method = "matchesMouse", at = @At("RETURN"), cancellable = true)
	public void matchesMouse(int mouse, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (!keyModifiers.isUnset() && !keyModifiers.equals(KeyModifiers.getCurrentlyPressed())) {
			callbackInfoReturnable.setReturnValue(false);
		}
	}

	@Inject(method = "same", at = @At("RETURN"), cancellable = true)
	public void same(KeyMapping other, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (!keyModifiers.equals(((IKeyBinding) other).amecs$getKeyModifiers())) {
			callbackInfoReturnable.setReturnValue(false);
		}
	}

	@Inject(method = "click", at = @At("HEAD"), cancellable = true)
	private static void onKeyPressed(InputConstants.Key keyCode, CallbackInfo callbackInfo) {
		KeyBindingManager.onKeyPressed(keyCode);
		callbackInfo.cancel();
	}

	@Inject(method = "set", at = @At("HEAD"), cancellable = true)
	private static void setKeyPressed(InputConstants.Key keyCode, boolean pressed, CallbackInfo callbackInfo) {
		KeyBindingManager.setKeyPressed(keyCode, pressed);
		callbackInfo.cancel();
	}

	@Inject(method = "setAll", at = @At("HEAD"), cancellable = true)
	private static void updatePressedStates(CallbackInfo callbackInfo) {
		KeyBindingManager.updatePressedStates();
		callbackInfo.cancel();
	}

	@Inject(method = "resetMapping", at = @At("HEAD"), cancellable = true)
	private static void updateKeysByCode(CallbackInfo callbackInfo) {
		KeyBindingManager.updateKeysByCode();
		callbackInfo.cancel();
	}

	@Inject(method = "releaseAll", at = @At("HEAD"), cancellable = true)
	private static void unpressAll(CallbackInfo callbackInfo) {
		KeyBindingManager.unpressAll();
		callbackInfo.cancel();
	}

	@Inject(method = "isDefault", at = @At("HEAD"), cancellable = true)
	public void isDefault(CallbackInfoReturnable<Boolean> cir) {
		if (!((Object) this instanceof AmecsKeyBinding)) {
			if (!keyModifiers.isUnset()) {
				cir.setReturnValue(false);
			}
		}
	}

	@SuppressWarnings("unused")
	private static Map<String, KeyMapping> amecs$getIdToKeyBindingMap() {
		return ALL;
	}
}
