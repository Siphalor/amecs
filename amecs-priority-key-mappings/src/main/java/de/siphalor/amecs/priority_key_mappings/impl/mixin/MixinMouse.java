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

import de.siphalor.amecs.priority_key_mappings.impl.AmecsPriorityKeyMappingsModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;

// TODO: Fix the priority when Mixin 0.8 is a thing and try again (-> MaLiLib causes incompatibilities)
@Environment(EnvType.CLIENT)
@Mixin(value = MouseHandler.class, priority = -2000)
public class MixinMouse {
	@Inject(
			//# if MC_VERSION_NUMBER >= 12109
			method = "onButton",
			//# else
			//- method = "onPress",
			//# end
			at = @At(
					//# if MC_VERSION_NUMBER >= 260200
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/Gui;overlay()Lnet/minecraft/client/gui/screens/Overlay;",
					//# else
					//- value = "FIELD",
					//- target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;",
					//# end
					ordinal = 0,
					opcode = Opcodes.GETFIELD
			),
			cancellable = true
	)
	//# if MC_VERSION_NUMBER >= 12109
	private void onMouseButtonPriority(long window, MouseButtonInfo event, int state, CallbackInfo callbackInfo) {
		InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(event.button());
	//# else
	//- private void onMouseButtonPriority(long window, int type, int state, int int_3, CallbackInfo callbackInfo) {
	//- 		InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(type);
	//# end
		if (state == 1 && AmecsPriorityKeyMappingsModule.onPressedPriority(key)) {
			callbackInfo.cancel();
		} else if (state == 0 && AmecsPriorityKeyMappingsModule.onReleasedPriority(key)) {
			callbackInfo.cancel();
		}
	}
}
