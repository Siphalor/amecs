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

import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifier;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

public class DefaultKeyModifier extends AmecsKeyModifier {
	@Getter
	private final @Nullable Integer legacyId;
	private final int[] keyCodes;

	public DefaultKeyModifier(String name, @Nullable Integer legacyId, int... keyCodes) {
		super(name);
		this.legacyId = legacyId;
		this.keyCodes = keyCodes;
	}

	@Override
	public boolean matches(int keyCode) {
		return ArrayUtils.contains(keyCodes, keyCode);
	}
}
