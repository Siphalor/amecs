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

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.impl.KeyBindingEditGuiHelper;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
//# else
//- import net.minecraft.client.gui.screens.OptionsSubScreen;
//- import net.minecraft.client.gui.screens.controls.ControlList;
//- import net.minecraft.client.gui.screens.controls.ControlsScreen;
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
//- import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
//# end
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
//- import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.duck.IKeyBinding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@SuppressWarnings("WeakerAccess")
@Environment(EnvType.CLIENT)
//# if MC_VERSION_NUMBER >= 11800
@Mixin(KeyBindsScreen.class)
//# else
//- @Mixin(ControlsScreen.class)
//# end
public abstract class MixinKeybindsScreen
		extends /*# if MC_VERSION_NUMBER >= 11500 */OptionsSubScreen/*# else *//*- Screen *//*# end */ {
	@Shadow
	public KeyMapping selectedKey;

	@Shadow
	public long lastKeySelection;

	//# if MC_VERSION_NUMBER >= 11904
	@Shadow private KeyBindsList keyBindsList;
	//# end

	//# if MC_VERSION_NUMBER < 11500
	//- @Shadow @Final
	//- private Options options;
	//#end

	//# if MC_VERSION_NUMBER >= 11500
	public MixinKeybindsScreen(Screen screen, Options gameOptions, Component text) {
		super(screen, gameOptions, text);
	}
	//# else
	//- public MixinKeybindsScreen(Component title) {
	//- 	super(title);
	//- }
	//# end

	@Inject(
			method = "mouseClicked",
			//# if MC_VERSION_NUMBER >= 12102
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setKey(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V")
			//# else
			//- at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setKey(Lnet/minecraft/client/KeyMapping;Lcom/mojang/blaze3d/platform/InputConstants$Key;)V")
			//# end
	)
	public void onClicked(
			//# if MC_VERSION_NUMBER >= 12109
			MouseButtonEvent mouseButtonEvent,
			boolean bl,
			//# else
			//- double x,
			//- double y,
			//- int type,
			//# end
			CallbackInfoReturnable<Boolean> callbackInfoReturnable
	) {
		InputConstants.Key key = ((IKeyBinding) selectedKey).amecs$getBoundKey();
		KeyModifiers keyModifiers = ((IKeyBinding) selectedKey).amecs$getKeyModifiers();
		if (!key.equals(InputConstants.UNKNOWN)) {
			keyModifiers.set(KeyModifier.fromKey(key), true);
		}
	}

	@Inject(
			method = "keyPressed",
			at = @At(
					value = "INVOKE",
					//# if MC_VERSION_NUMBER >= 12102
					target = "Lnet/minecraft/client/KeyMapping;setKey(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V",
					//# else
					//- target = "Lnet/minecraft/client/Options;setKey(Lnet/minecraft/client/KeyMapping;Lcom/mojang/blaze3d/platform/InputConstants$Key;)V",
					//# end
					ordinal = 1
			),
			cancellable = true
	)
	public void onKeyPressed(
			//# if MC_VERSION_NUMBER >= 12109
			KeyEvent keyEvent,
			//# else
			//- int keyCode,
			//- int scanCode,
			//- int modifiers,
			//# end
			CallbackInfoReturnable<Boolean> callbackInfoReturnable
	) {
		//# if MC_VERSION_NUMBER >= 12109
		InputConstants.Key key = InputConstants.getKey(keyEvent);
		//# else
		//- InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
		//# end

		KeyBindingEditGuiHelper.handleKeyPress(selectedKey, key);

		this.lastKeySelection = Util.getMillis();
		//# if MC_VERSION_NUMBER >= 11904
		this.keyBindsList.resetMappingAndUpdateButtons();
		//# else
		//- KeyMapping.resetMapping();
		//# end
		callbackInfoReturnable.setReturnValue(true);
	}
}
