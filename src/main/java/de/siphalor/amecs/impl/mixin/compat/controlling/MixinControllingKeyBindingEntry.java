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

package de.siphalor.amecs.impl.mixin.compat.controlling;

//- import com.blamejared.controlling.client.NewKeyBindsList;
//- import com.mojang.blaze3d.platform.InputConstants;
//- import net.minecraft.client.KeyMapping;
//- import net.minecraft.client.gui.components.Button;
//- import org.spongepowered.asm.mixin.Mixin;
//- import org.spongepowered.asm.mixin.injection.At;
//- import org.spongepowered.asm.mixin.injection.Inject;
//- import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//# if CONTROLLING_INTEGRATION
//- @Mixin(value = NewKeyBindsList.KeyEntry.class, remap = false)
//- public abstract class MixinControllingKeyBindingEntry {
//- 	@Inject(method = "lambda$new$0", at = @At("HEAD"))
//- 	public void onEditButtonClicked(KeyMapping keyBinding, Button button, CallbackInfo callbackInfo) {
//- 		keyBinding.setKey(InputConstants.UNKNOWN);
//- 	}
//- }
//# end
