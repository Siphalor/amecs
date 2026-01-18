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

package de.siphalor.amecs.api;

import de.siphalor.amecs.impl.OldKeyModifiersView;
import de.siphalor.amecs.key_modifiers.impl.AmecsKeyModifiersModule;
import de.siphalor.amecs.key_modifiers.impl.duck.IKeyMapping;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

/**
 * Defines modifiers for a key binding
 * @deprecated Use {@link de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination} instead.
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
@Environment(EnvType.CLIENT)
@Deprecated(forRemoval = true)
public class KeyModifiers {
	/**
	 * This field is for comparison ONLY.
	 * <p>
	 * Trying to change the modifiers of it will fail with an {@link UnsupportedOperationException}
	 */
	public static final KeyModifiers NO_MODIFIERS = new FinalKeyModifiers();

	private static class FinalKeyModifiers extends KeyModifiers {
		private static final String EXCEPTION_MESSAGE = "You must not alter this Modifiers object";

		@Override
		public KeyModifiers setValue(boolean[] value) {
			throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
		}

		@Override
		public void set(KeyModifier keyModifier, boolean value) {
			throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
		}

		@Override
		public void unset() {
			throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
		}
	}

	public static KeyModifiers getCurrentlyPressed() {
		return new OldKeyModifiersView(AmecsKeyModifiersModule.CURRENT_MODIFIERS);
	}

	// using a boolean array here because it is faster and needs less space
	private final boolean[] value;

	/**
	 * Constructs new object with no modifiers set
	 */
	public KeyModifiers() {
		this(new boolean[KeyModifier.getModifierCount()]);
	}

	/**
	 * FOR INTERNAL USE ONLY
	 * <p>
	 * Constructs a new modifier object by a raw boolean array
	 *
	 * @param value the raw value with flags set
	 */
	@ApiStatus.Internal
	public KeyModifiers(boolean[] value) {
		if (value.length != KeyModifier.getModifierCount()) {
			throw new IllegalArgumentException("value.length != KeyModifier.getModifierCount(): " + KeyModifier.getModifierCount());
		}
		this.value = value;
	}

	/**
	 * Constructs a new modifier object by all modifier bits
	 *
	 * @param alt     sets whether the alt flag should be set
	 * @param control sets whether the control flag should be set
	 * @param shift   sets whether the shift flag should be set
	 */
	public KeyModifiers(boolean alt, boolean control, boolean shift) {
		this();
		setAlt(alt);
		setControl(control);
		setShift(shift);
	}

	/**
	 * Compares this object with the currently pressed keys
	 *
	 * @return whether the modifiers match in the current context
	 * @deprecated always performs an exact check against {@link #getCurrentlyPressed()}.
	 */
	@Deprecated
	public boolean isPressed() {
		return equals(new OldKeyModifiersView(AmecsKeyModifiersModule.CURRENT_MODIFIERS));
	}

	/**
	 * Returns whether the given modifiers are also set in this object.
	 * @param other the modifiers to check
	 * @return whether the given modifiers are also set in this object
	 */
	public boolean contains(KeyModifiers other) {
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
	 * Sets the raw value
	 *
	 * @param value the value with flags set
	 */
	@ApiStatus.Internal
	public KeyModifiers setValue(boolean[] value) {
		int length = this.value.length;
		if (value.length != length) {
			throw new IllegalArgumentException("value != this.value.length: " + length);
		}
		System.arraycopy(value, 0, this.value, 0, length);
		return this;
	}

	/**
	 * FOR INTERNAL USE ONLY
	 * <p>
	 * Gets the raw value
	 *
	 * @return the value with all flags set
	 */
	@ApiStatus.Internal
	public boolean[] getValue() {
		return value;
	}

	/**
	 * FOR INTERNAL USE ONLY
	 * <p>
	 * copies the modifiers of the other KeyModifiers object into this
	 */
	@ApiStatus.Internal
	public void copyModifiers(KeyModifiers other) {
		setValue(other.getValue());
	}

	/**
	 * Sets the alt flag
	 *
	 * @param value whether the alt flag should be activated or not
	 */
	public KeyModifiers setAlt(boolean value) {
		set(KeyModifier.ALT, value);
		return this;
	}

	/**
	 * Gets the state of the alt flag
	 *
	 * @return whether the alt key needs to be pressed
	 */
	public boolean getAlt() {
		return get(KeyModifier.ALT);
	}

	/**
	 * Sets the control flag
	 *
	 * @param value whether the control flag should be activated or not
	 */
	public KeyModifiers setControl(boolean value) {
		set(KeyModifier.CONTROL, value);
		return this;
	}

	/**
	 * Gets the state of the control flag
	 *
	 * @return whether the control key needs to be pressed
	 */
	public boolean getControl() {
		return get(KeyModifier.CONTROL);
	}

	/**
	 * Sets the shift flag
	 *
	 * @param value whether the shift flag should be activated or not
	 */
	public KeyModifiers setShift(boolean value) {
		set(KeyModifier.SHIFT, value);
		return this;
	}

	/**
	 * Gets the state of the shift flag
	 *
	 * @return whether the shift key needs to be pressed
	 */
	public boolean getShift() {
		return get(KeyModifier.SHIFT);
	}

	public void set(KeyModifier keyModifier, boolean value) {
		if (keyModifier != KeyModifier.NONE) {
			this.value[keyModifier.id] = value;
		}
	}

	public boolean get(KeyModifier keyModifier) {
		if (keyModifier == KeyModifier.NONE) {
			return true;
		}
		return value[keyModifier.id];
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
		set(KeyModifier.fromKey(key), false);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof KeyModifiers) {
			return equals((KeyModifiers) obj);
		}
		return false;
	}

	/**
	 * Returns whether this object equals another one
	 *
	 * @param other another modifier object
	 * @return whether both values are equal
	 */
	public boolean equals(KeyModifiers other) {
		return Arrays.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "KeyModifiers [alt=" + getAlt() + ", control=" + getControl() + ", shift=" + getShift() + "]";
	}
}
