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

import de.siphalor.amecs.key_modifiers.impl.AmecsKeyModifiersModule;
import de.siphalor.amecs.key_modifiers.impl.duck.IKeyMapping;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

/**
 * Defines modifiers for a key binding
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
@Environment(EnvType.CLIENT)
public class AmecsKeyModifierCombination {
	/**
	 * This field is for comparison ONLY.
	 * <p>
	 * Trying to change the modifiers of it will fail with an {@link UnsupportedOperationException}
	 */
	public static final AmecsKeyModifierCombination NO_MODIFIERS = new Immutable();

	private static class Immutable extends AmecsKeyModifierCombination {
		private static final String EXCEPTION_MESSAGE = "You must not alter this Modifiers object";

		@Override
		public AmecsKeyModifierCombination setAlt(boolean value) {
			throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
		}

		@Override
		public AmecsKeyModifierCombination setControl(boolean value) {
			throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
		}

		@Override
		public AmecsKeyModifierCombination setShift(boolean value) {
			throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
		}

		@Override
		public void copyFrom(AmecsKeyModifierCombination other) {
			throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
		}

		@Override
		public void set(AmecsKeyModifier keyModifier, boolean value) {
			throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
		}

		@Override
		public void unset() {
			throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
		}
	}

	public static AmecsKeyModifierCombination getCurrentlyPressed() {
		return AmecsKeyModifiersModule.CURRENT_MODIFIERS;
	}

	// using a boolean array here because it is faster
	private boolean[] value;

	/**
	 * Constructs a new modifier object by all modifier bits
	 *
	 * @param alt     sets whether the alt flag should be set
	 * @param control sets whether the control flag should be set
	 * @param shift   sets whether the shift flag should be set
	 */
	public AmecsKeyModifierCombination(boolean alt, boolean control, boolean shift) {
		this();
		setAlt(alt);
		setControl(control);
		setShift(shift);
	}

	public AmecsKeyModifierCombination(AmecsKeyModifier... modifiers) {
		this();

		for (AmecsKeyModifier modifier : modifiers) {
			requireModifierRegistered(modifier);
			set(modifier, true);
		}
	}

	/**
	 * Constructs a new object with no modifiers set
	 */
	public AmecsKeyModifierCombination() {
		this(new boolean[AmecsKeyModifiers.MODIFIERS.size()]);
	}

	private AmecsKeyModifierCombination(boolean[] value) {
		if (value.length > AmecsKeyModifiers.MODIFIERS.size()) {
			throw new IllegalArgumentException("value.length > KeyModifier.getModifierCount(): " + AmecsKeyModifiers.MODIFIERS.size());
		}
		this.value = value;
	}

	/**
	 * Returns whether the given modifiers are also set in this object.
	 * @param other the modifiers to check
	 * @return whether the given modifiers are also set in this object
	 */
	public boolean contains(AmecsKeyModifierCombination other) {
		for (int i = 0; i < value.length; i++) {
			if (other.value[i] && !this.value[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * FOR INTERNAL USE ONLY
	 * <p>
	 * copies the modifiers of the other KeyModifiers object into this
	 */
	@ApiStatus.Internal
	public void copyFrom(AmecsKeyModifierCombination other) {
		value = other.value.clone();
	}

	/**
	 * Sets the alt flag
	 *
	 * @param value whether the alt flag should be activated or not
	 */
	public AmecsKeyModifierCombination setAlt(boolean value) {
		setUnchecked(AmecsKeyModifiers.ALT, value);
		return this;
	}

	/**
	 * Gets the state of the alt flag
	 *
	 * @return whether the alt key needs to be pressed
	 */
	public boolean getAlt() {
		return getUnchecked(AmecsKeyModifiers.ALT);
	}

	/**
	 * Sets the control flag
	 *
	 * @param value whether the control flag should be activated or not
	 */
	public AmecsKeyModifierCombination setControl(boolean value) {
		setUnchecked(AmecsKeyModifiers.CONTROL, value);
		return this;
	}

	/**
	 * Gets the state of the control flag
	 *
	 * @return whether the control key needs to be pressed
	 */
	public boolean getControl() {
		return getUnchecked(AmecsKeyModifiers.CONTROL);
	}

	/**
	 * Sets the shift flag
	 *
	 * @param value whether the shift flag should be activated or not
	 */
	public AmecsKeyModifierCombination setShift(boolean value) {
		setUnchecked(AmecsKeyModifiers.SHIFT, value);
		return this;
	}

	/**
	 * Gets the state of the shift flag
	 *
	 * @return whether the shift key needs to be pressed
	 */
	public boolean getShift() {
		return getUnchecked(AmecsKeyModifiers.SHIFT);
	}

	public void set(AmecsKeyModifier keyModifier, boolean value) {
		if (keyModifier.arrayIndex >= this.value.length) {
			requireModifierRegistered(keyModifier);
			this.value = Arrays.copyOf(this.value, AmecsKeyModifiers.MODIFIERS.size());
		}
		setUnchecked(keyModifier, value);
	}

	public boolean get(AmecsKeyModifier keyModifier) {
		requireModifierRegistered(keyModifier);
		if (keyModifier.arrayIndex >= value.length) return false;
		return getUnchecked(keyModifier);
	}

	private void requireModifierRegistered(AmecsKeyModifier keyModifier) {
		if (!keyModifier.isRegistered()) {
			throw new IllegalArgumentException("Modifier " + keyModifier.getTranslationKey() + " is not registered yet");
		}
	}

	private void setUnchecked(AmecsKeyModifier keyModifier, boolean value) {
		this.value[keyModifier.arrayIndex] = value;
	}

	private boolean getUnchecked(AmecsKeyModifier keyModifier) {
		return value[keyModifier.arrayIndex];
	}

	public Collection<AmecsKeyModifier> getAll() {
		List<AmecsKeyModifier> result = new ArrayList<>(AmecsKeyModifiers.MODIFIERS.size());
		for (int i = 0; i < this.value.length; i++) {
			if (this.value[i]) {
				result.add(AmecsKeyModifiers.MODIFIERS.get(i));
			}
		}
		return result;
	}

	/**
	 * Returns whether no flag is set
	 *
	 * @return value == 0
	 */
	public boolean isUnset() {
		return !ArrayUtils.contains(value, true);
	}

	/**
	 * Clears all flags
	 */
	public void unset() {
		Arrays.fill(value, false);
	}

	/**
	 * Cleans up the flags by the key code present in the given key binding
	 *
	 * @param keyBinding the key binding from where to extract the key code
	 */
	public void cleanup(KeyMapping keyBinding) {
		InputConstants.Key key = ((IKeyMapping) keyBinding).amecs$getBoundKey();
		AmecsKeyModifier keyModifier = AmecsKeyModifiers.fromKey(key);
		if (keyModifier != null) {
			set(keyModifier, false);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AmecsKeyModifierCombination) {
			return equals((AmecsKeyModifierCombination) obj);
		}
		return false;
	}

	/**
	 * Returns whether this object equals another one
	 *
	 * @param other another modifier object
	 * @return whether both values are equal
	 */
	public boolean equals(AmecsKeyModifierCombination other) {
		return Arrays.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "AmecsK [alt=" + getAlt() + ", control=" + getControl() + ", shift=" + getShift() + "]";
	}

	// new format even if it needs more characters because it is more user friendly (and simpler to parse). Not everyone knows about bit masks
	// it could be discussed whether this new is really "better" but i leave it for now. It is backward compatible so nothing breaks

}
