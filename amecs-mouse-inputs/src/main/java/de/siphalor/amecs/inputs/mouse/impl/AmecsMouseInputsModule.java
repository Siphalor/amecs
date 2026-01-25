/*
 * Copyright 2026 Siphalor
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

package de.siphalor.amecs.inputs.mouse.impl;

import de.siphalor.amecs.inputs.mouse.api.AmecsMouseInputs;
import net.fabricmc.loader.api.FabricLoader;

import com.mojang.blaze3d.platform.InputConstants;

public class AmecsMouseInputsModule {
	public static final String MOD_ID = "amecs_mouse_inputs";
	public static final boolean PRIORITY_MODULE_PRESENT = FabricLoader.getInstance().isModLoaded("amecs_priority_key_mappings");

	public static String makeKeyID(String keyName) {
		return "key." + MOD_ID + "." + keyName;
	}

	public static InputConstants.Key getKeyFromVerticalScroll(double deltaY) {
		if (deltaY == 0D) {
			return null;
		}
		return InputConstants.Type.MOUSE.getOrCreate(deltaY > 0 ? AmecsMouseInputs.SCROLL_UP : AmecsMouseInputs.SCROLL_DOWN);
	}

	//# if MC_VERSION_NUMBER >= 12002
	public static InputConstants.Key getKeyFromHorizontalScroll(double deltaX) {
		if (deltaX == 0D) {
			return null;
		}
		return InputConstants.Type.MOUSE.getOrCreate(deltaX > 0 ? AmecsMouseInputs.SCROLL_RIGHT : AmecsMouseInputs.SCROLL_LEFT);
	}
	//# end
}
