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

package de.siphalor.amecs.key_modifiers.api;

import de.siphalor.amecs.key_modifiers.impl.ModifierPrefixTextProvider;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class AmecsKeyModifier {
	@Getter
	private final String name;
	@Getter
	private final ModifierPrefixTextProvider textProvider;
	int arrayIndex = -1;

	public AmecsKeyModifier(String name) {
		this.name = name;
		this.textProvider = new ModifierPrefixTextProvider(this);
	}

	boolean isRegistered() {
		return arrayIndex != -1;
	}

	/**
	 * This is the legacy id of the key modifier that is used for reading old config files.
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public @Nullable Integer getLegacyId() {
		return null;
	}

	public abstract boolean matches(int keyCode);

	public String getTranslationKey() {
		return "amecs.key-modifiers.modifier." + name;
	}
}
