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

//- import de.siphalor.amecs.impl.AmecsAPI;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiersApi;
import de.siphalor.amecs.key_modifiers.impl.duck.IKeyBindingEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.*;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
//# else
//- import net.minecraft.client.gui.screens.controls.ControlList;
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
//# end

@SuppressWarnings("WeakerAccess")
@Environment(EnvType.CLIENT)
//# if MC_VERSION_NUMBER >= 11800
@Mixin(KeyBindsList.KeyEntry.class)
//# else
//- @Mixin(ControlList.KeyEntry.class)
//# end
public abstract class MixinKeyBindingEntry
		//# if MC_VERSION_NUMBER >= 11800
		extends KeyBindsList.Entry
		//# else
		//- extends ControlList.Entry
		//# end
		implements IKeyBindingEntry {
	@Shadow
	@Final
	private KeyMapping key;

	@Inject(
			method = "method_19870(Lnet/minecraft/client/KeyMapping;Lnet/minecraft/client/gui/components/Button;)V",
			at = @At("HEAD")
	)
	public void onResetButtonClicked(KeyMapping keyBinding, Button buttonWidget, CallbackInfo callbackInfo) {
		AmecsKeyModifiersApi.resetBoundModifiers(keyBinding);
	}

	@Inject(method = "method_19871(Lnet/minecraft/client/KeyMapping;Lnet/minecraft/client/gui/components/Button;)V", at = @At("HEAD"))
	public void onEditButtonClicked(KeyMapping keyBinding, Button buttonWidget, CallbackInfo callbackInfo) {
		key.setKey(InputConstants.UNKNOWN);
	}

	@Override
	public KeyMapping amecs$getKeyBinding() {
		return key;
	}
}
