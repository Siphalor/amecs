/*
 * Copyright 2020 Siphalor
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

package de.siphalor.amecs.key_modifiers.impl.mixin;

//- import com.llamalad7.mixinextras.sugar.Local;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyMappingWithKeyModifiers;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifier;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers;
import de.siphalor.amecs.key_modifiers.impl.AmecsKeyModifiersModule;
import de.siphalor.amecs.key_modifiers.impl.ModifierPrefixTextProvider;
import de.siphalor.amecs.key_modifiers.impl.duck.IKeyMapping;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//- import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//- import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

@SuppressWarnings("WeakerAccess")
@Environment(EnvType.CLIENT)
@Mixin(KeyMapping.class)
public abstract class MixinKeyMapping implements IKeyMapping {
	@Shadow
	private InputConstants.Key key;

	@Unique
	private final AmecsKeyModifierCombination keyModifiers = new AmecsKeyModifierCombination();

	@Override
	public AmecsKeyModifierCombination amecs$getBoundKeyModifiers() {
		return keyModifiers;
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
			for (AmecsKeyModifier keyModifier : AmecsKeyModifiers.getAll()) {
				if (keyModifiers.get(keyModifier)) {
					fullName = keyModifier.getTextProvider().getComponent(variation).append(fullName);
				}
			}
		} while ((variation = variation.getSmaller()) != null && font.width(fullName) > 70);

		callbackInfoReturnable.setReturnValue(fullName);
	}
	//# else
	//- @Inject(method = "getTranslatedKeyMessage", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	//- public void getLocalizedName(
	//- 		CallbackInfoReturnable<String> callbackInfoReturnable,
	//- 		@Local(ordinal = 0) String i18nName,
	//- 		//# if MC_VERSION_NUMBER >= 11600
	//- 		@Local(ordinal = 2) String glfwName
	//- 		//# else
	//- 		@Local(ordinal = 1) String glfwName
	//- 		//# end
	//- ) {
	//- 	String name = (glfwName == null ? I18n.get(i18nName) : glfwName);
	//- 	StringBuilder fullName;
	//- 	Font font = Minecraft.getInstance().font;
	//- 	ModifierPrefixTextProvider.Variation variation = ModifierPrefixTextProvider.Variation.WIDEST;
	//- 	do {
	//- 		fullName = new StringBuilder(name);
	//- 		for (AmecsKeyModifier keyModifier : AmecsKeyModifiers.getAll()) {
	//- 			if (keyModifiers.get(keyModifier)) {
	//- 				fullName.insert(0, keyModifier.getTextProvider().getComponent(variation));
	//- 			}
	//- 		}
	//- 	} while ((variation = variation.getSmaller()) != null && font.width(fullName.toString()) > 70);
	//- 	callbackInfoReturnable.setReturnValue(fullName.toString());
	//- }
	//# end

	@Inject(method = "matches", at = @At("RETURN"), cancellable = true)
	public void matchesKey(
			//# if MC_VERSION_NUMBER >= 12109
			KeyEvent event,
			//# else
			//- int keyCode,
			//- int scanCode,
			//# end
			CallbackInfoReturnable<Boolean> callbackInfoReturnable
	) {
		if (!keyModifiers.isUnset() && !keyModifiers.equals(AmecsKeyModifiersModule.CURRENT_MODIFIERS)) {
			callbackInfoReturnable.setReturnValue(false);
		}
	}

	@Inject(method = "matchesMouse", at = @At("RETURN"), cancellable = true)
	public void matchesMouse(
			//# if MC_VERSION_NUMBER >= 12109
			MouseButtonEvent event,
			//# else
			//- int mouse,
			//# end
			CallbackInfoReturnable<Boolean> callbackInfoReturnable
	) {
		if (!keyModifiers.isUnset() && !keyModifiers.equals(AmecsKeyModifiersModule.CURRENT_MODIFIERS)) {
			callbackInfoReturnable.setReturnValue(false);
		}
	}

	@Inject(method = "same", at = @At("RETURN"), cancellable = true)
	public void same(KeyMapping other, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		if (!keyModifiers.equals(((IKeyMapping) other).amecs$getBoundKeyModifiers())) {
			callbackInfoReturnable.setReturnValue(false);
		}
	}

	@Inject(method = "setKey", at = @At("RETURN"))
	public void setKey(InputConstants.Key key, CallbackInfo callbackInfo) {
		if (key == InputConstants.UNKNOWN) {
			keyModifiers.unset();
		}
	}

	@Inject(method = "isDefault", at = @At("HEAD"), cancellable = true)
	public void isDefault(CallbackInfoReturnable<Boolean> cir) {
		//noinspection ConstantValue
		if (!((Object) this instanceof AmecsKeyMappingWithKeyModifiers)) {
			if (!keyModifiers.isUnset()) {
				cir.setReturnValue(false);
			}
		}
	}
}
