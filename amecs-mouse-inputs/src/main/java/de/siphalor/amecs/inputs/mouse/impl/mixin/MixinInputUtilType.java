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

package de.siphalor.amecs.inputs.mouse.impl.mixin;

import com.mojang.blaze3d.platform.InputConstants;
//- import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.inputs.mouse.api.AmecsMouseInputs;
import de.siphalor.amecs.inputs.mouse.impl.AmecsMouseInputsModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InputConstants.Type.class)
public abstract class MixinInputUtilType {
	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void onRegisterKeyCodes(CallbackInfo callbackInfo) {
		createScrollKey("scroll.up", AmecsMouseInputs.SCROLL_UP);
		createScrollKey("scroll.down", AmecsMouseInputs.SCROLL_DOWN);
		//# if MC_VERSION_NUMBER >= 12002
		createScrollKey("scroll.left", AmecsMouseInputs.SCROLL_LEFT);
		createScrollKey("scroll.right", AmecsMouseInputs.SCROLL_RIGHT);
		//# end
	}

	@Unique
	private static void createScrollKey(String name, int keyCode) {
		String keyName = AmecsMouseInputsModule.makeKeyID(name);
		InputConstants.Type.addKey(InputConstants.Type.MOUSE, keyName, keyCode);

		// Legacy compatibility (amecsapi)
		InputConstants.Key.NAME_MAP.put("key.amecsapi.mouse." + name, InputConstants.getKey(keyName));
		InputConstants.Key.NAME_MAP.put("amecsapi.key.mouse." + name, InputConstants.getKey(keyName));
	}
}
