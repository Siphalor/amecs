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

package de.siphalor.amecs.key_modifiers.impl;

import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
//- import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers;
import de.siphalor.amecs.key_modifiers.impl.compat.controlling.AmecsControllingIntegration;
//- import java.lang.reflect.InvocationTargetException;
//- import java.lang.reflect.Method;
//- import java.util.Map;
import lombok.CustomLog;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

//- import net.minecraft.client.KeyMapping;

@CustomLog
public class AmecsKeyModifiersModule implements ClientModInitializer {
	public static final String MOD_ID = "amecs_key_modifiers";
	public static final AmecsKeyModifierCombination CURRENT_MODIFIERS = new AmecsKeyModifierCombination();

	@Override
	public void onInitializeClient() {
		//# if CONTROLLING_INTEGRATION
		FabricLoader loader = FabricLoader.getInstance();
		if (loader.isModLoaded("controlling")) {
			AmecsControllingIntegration.initialize();
		}
		//# end
	}
}
