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

import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifier;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers;
import de.siphalor.amecs.key_modifiers.impl.AmecsKeyModifiersModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
//# else
//- import net.minecraft.client.gui.screens.controls.ControlsScreen;
//- import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
//# end
import net.minecraft.client.input.KeyEvent;

@Environment(EnvType.CLIENT)
@Mixin(KeyboardHandler.class)
public class MixinKeyboard {
	@Inject(method = "keyPress", at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/KeyboardHandler;debugCrashKeyTime:J",
			ordinal = 0,
			opcode = Opcodes.GETFIELD
	))
	private void onKey(
			long window,
			//# if MC_VERSION_NUMBER < 12109
			//- int keyCode,
			//- int scanCode,
			//# end
			int action,
			//# if MC_VERSION_NUMBER >= 12109
			KeyEvent keyEvent,
			//# else
			//- int modifiers,
			//# end
			CallbackInfo callbackInfo
	) {
		// Key released
		//# if MC_VERSION_NUMBER >= 11800
		if (action == 0 && Minecraft.getInstance().screen instanceof KeyBindsScreen screen) {
		//# else
		//- if (action == 0 && Minecraft.getInstance().screen instanceof ControlsScreen) {
		//- 	ControlsScreen screen = (ControlsScreen) Minecraft.getInstance().screen;
		//# end

			screen.selectedKey = null;
			screen.lastKeySelection = Util.getMillis();
		}

		//# if MC_VERSION_NUMBER >= 12109
		InputConstants.Key key = InputConstants.getKey(keyEvent);
		//# else
		//- InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
		//# end
		AmecsKeyModifier keyModifier = AmecsKeyModifiers.fromKeyCode(key.getValue());
		if (keyModifier != null) {
			AmecsKeyModifiersModule.CURRENT_MODIFIERS.set(keyModifier, action != 0);
		}
	}
}
