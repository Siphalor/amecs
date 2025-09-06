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
import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.impl.AmecsAPI;
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
		createScrollKey("mouse.scroll.up", KeyBindingUtils.MOUSE_SCROLL_UP);
		createScrollKey("mouse.scroll.down", KeyBindingUtils.MOUSE_SCROLL_DOWN);
		//# if MC_VERSION_NUMBER >= 12002
		createScrollKey("mouse.scroll.left", KeyBindingUtils.MOUSE_SCROLL_LEFT);
		createScrollKey("mouse.scroll.right", KeyBindingUtils.MOUSE_SCROLL_RIGHT);
		//# end
	}

	@Unique
	private static void createScrollKey(String name, int keyCode) {
		String keyName = AmecsAPI.makeKeyID(name);
		InputConstants.Type.addKey(InputConstants.Type.MOUSE, keyName, keyCode);

		// Legacy compatibility (amecsapi <1.3)
		InputConstants.Key.NAME_MAP.put("amecsapi.key." + name, InputConstants.getKey(keyName));
	}
}
