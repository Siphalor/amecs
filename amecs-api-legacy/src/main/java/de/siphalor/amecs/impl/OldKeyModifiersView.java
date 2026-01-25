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

package de.siphalor.amecs.impl;

import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifier;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers;

import net.minecraft.client.KeyMapping;

@SuppressWarnings("removal")
public class OldKeyModifiersView extends KeyModifiers {
	private final AmecsKeyModifierCombination newKeyModifiers;

	public OldKeyModifiersView(AmecsKeyModifierCombination newKeyModifiers) {
		this.newKeyModifiers = newKeyModifiers;
		setAlt(newKeyModifiers.getAlt());
		setControl(newKeyModifiers.getControl());
		setShift(newKeyModifiers.getShift());

		AmecsLegacyDeprecationEmitter.invoke();
	}

	@Override
	public KeyModifiers setValue(boolean[] value) {
		super.setValue(value);
		newKeyModifiers.setAlt(getAlt()).setControl(getControl()).setShift(getShift());
		return this;
	}

	@Override
	public void unset() {
		super.unset();
		newKeyModifiers.unset();
	}

	@Override
	public void cleanup(KeyMapping keyBinding) {
		super.cleanup(keyBinding);
		newKeyModifiers.cleanup(keyBinding);
	}

	@Override
	public boolean get(KeyModifier keyModifier) {
		return newKeyModifiers.get(mapToNewKeyModifier(keyModifier));
	}

	@Override
	public void set(KeyModifier keyModifier, boolean value) {
		super.set(keyModifier, value);
		AmecsKeyModifier modifier = mapToNewKeyModifier(keyModifier);
		if (modifier == null) return;
		newKeyModifiers.set(modifier, value);
	}

	private static AmecsKeyModifier mapToNewKeyModifier(KeyModifier keyModifier) {
		//noinspection EnhancedSwitchMigration
		switch (keyModifier) {
			case ALT: return AmecsKeyModifiers.ALT;
			case SHIFT: return AmecsKeyModifiers.SHIFT;
			case CONTROL: return AmecsKeyModifiers.CONTROL;
			default: return null;
		}
	}
}
