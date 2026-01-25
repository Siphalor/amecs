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

import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Special "early" initializer for key modifiers.
 * @implNote This uses Fabric's common initializer because that runs before client initializers
 * where key mappings are usually registered.
 */
public class AmecsKeyModifiersEarlyInit implements ModInitializer {
	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			AmecsKeyModifiers.register(AmecsKeyModifiers.ALT);
			AmecsKeyModifiers.register(AmecsKeyModifiers.SHIFT);
			AmecsKeyModifiers.register(AmecsKeyModifiers.CONTROL);
			AmecsKeyModifiers.seal();
		}
	}
}
