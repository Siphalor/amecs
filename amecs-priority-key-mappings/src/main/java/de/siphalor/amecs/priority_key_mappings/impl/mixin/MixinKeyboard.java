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

package de.siphalor.amecs.priority_key_mappings.impl.mixin;

//- import de.siphalor.amecs.api.KeyModifier;
//- import de.siphalor.amecs.impl.AmecsAPI;
//- import de.siphalor.amecs.impl.KeyBindingManager;
import de.siphalor.amecs.priority_key_mappings.impl.AmecsPriorityKeyMappingsModule;
//- import de.siphalor.amecs.priority_key_mappings.impl.AmecsPriorityToKeyModifiersProxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
//- import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;

@Environment(EnvType.CLIENT)
@Mixin(KeyboardHandler.class)
public class MixinKeyboard {
	@Inject(method = "keyPress", at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;",
			ordinal = 0,
			opcode = Opcodes.GETFIELD
	), cancellable = true)
	private void onKeyPriority(
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
		//# if MC_VERSION_NUMBER >= 12109
		InputConstants.Key key = InputConstants.getKey(keyEvent);
		//# else
		//- InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
		//# end
		if (action == 1) {
			if (AmecsPriorityKeyMappingsModule.onPressedPriority(key)) {
				callbackInfo.cancel();
			}
		} else if (action == 0) {
			if (AmecsPriorityKeyMappingsModule.onReleasedPriority(key)) {
				callbackInfo.cancel();
			}
		}
	}
}
