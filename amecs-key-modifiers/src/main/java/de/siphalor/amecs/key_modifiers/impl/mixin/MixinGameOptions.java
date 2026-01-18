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

import de.siphalor.amecs.key_modifiers.impl.AmecsKeyModifierOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

@Environment(EnvType.CLIENT)
@Mixin(Options.class)
public class MixinGameOptions {
	@Shadow
	@Final
	public KeyMapping[] keyMappings;

	@Inject(method = "save", at = @At("RETURN"))
	public void write(CallbackInfo callbackInfo) {
		AmecsKeyModifierOptions.write(keyMappings);
	}

	@Inject(method = "load", at = @At("RETURN"))
	public void load(CallbackInfo callbackInfo) {
		AmecsKeyModifierOptions.read();
	}
}
