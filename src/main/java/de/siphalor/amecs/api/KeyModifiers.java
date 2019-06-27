package de.siphalor.amecs.api;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.util.IKeyBinding;
import net.minecraft.client.options.KeyBinding;

/**
 * Defines modifiers for a key binding
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class KeyModifiers {
	private char value;

	/**
	 * Constructs new object with no modifiers set
	 */
	public KeyModifiers() {
		this((char) 0);
	}

	/**
	 * Constructs a new modifier object by a raw value
	 * @param value the raw value with flags set
	 */
	public KeyModifiers(char value) {
		this.value = value;
	}

	/**
	 * Constructs a new modifier object by all modifier bits
	 * @param alt sets whether the alt flag should be set
	 * @param control sets whether the control flag should be set
	 * @param shift sets whether the shift flag should be set
	 */
	public KeyModifiers(boolean alt, boolean control, boolean shift) {
		this();
		setAlt(alt);
		setControl(control);
		setShift(shift);
	}

	/**
	 * Compares this object with the current pressed keys
	 * @return whether the modifiers match in the current context
	 */
	public boolean match() {
		return equals(Amecs.CURRENT_MODIFIERS);
	}

	/**
	 * Sets the raw value
	 * @param value the value with flags set
	 */
	public KeyModifiers setValue(char value) {
		this.value = value;
		return this;
	}

	/**
	 * Gets the raw value
	 * @return the value with all flags set
	 */
	public char getValue() {
		return value;
	}

	/**
	 * Sets the alt flag
	 * @param value whether the alt flag should be activated or not
	 */
	public KeyModifiers setAlt(boolean value) {
		this.value = Utils.setFlag(this.value, KeyModifier.ALT.flag, value);
		return this;
	}

	/**
	 * Gets the state of the alt flag
	 * @return whether the alt key needs to be pressed
	 */
	public boolean getAlt() {
		return Utils.getFlag(value, KeyModifier.ALT.flag);
	}

	/**
	 * Sets the control flag
	 * @param value whether the control flag should be activated or not
	 */
	public KeyModifiers setControl(boolean value) {
		this.value = Utils.setFlag(this.value, KeyModifier.CONTROL.flag, value);
		return this;
	}

	/**
	 * Gets the state of the control flag
	 * @return whether the control key needs to be pressed
	 */
	public boolean getControl() {
		return Utils.getFlag(value, KeyModifier.CONTROL.flag);
	}

	/**
	 * Sets the shift flag
	 * @param value whether the shift flag should be activated or not
	 */
	public KeyModifiers setShift(boolean value) {
		this.value = Utils.setFlag(this.value, KeyModifier.SHIFT.flag, value);
		return this;
	}

	/**
	 * Gets the state of the shift flag
	 * @return whether the shift key needs to be pressed
	 */
	public boolean getShift() {
		return Utils.getFlag(value, KeyModifier.SHIFT.flag);
	}

	public void set(KeyModifier keyModifier, boolean value) {
		this.value = Utils.setFlag(this.value, keyModifier.flag, value);
	}

	public boolean get(KeyModifier keyModifier) {
		return Utils.getFlag(this.value, keyModifier.flag);
	}

	/**
	 * Returns whether no flag is set
	 * @return value == 0
	 */
	public boolean isUnset() {
		return value == 0;
	}

	/**
	 * Clears all flags
	 */
	public void unset() {
		value = 0;
	}

	/**
	 * Cleans up the flags by the key code present in the given key binding
	 * @param keyBinding the key binding from where to extract the key code
	 */
	public void cleanup(KeyBinding keyBinding) {
		int keyCode = ((IKeyBinding) keyBinding).amecs$getKeyCode().getKeyCode();
		set(KeyModifier.fromKeyCode(keyCode), false);
	}

	/**
	 * Returns whether this object equals another one
	 * @param other another modifier object
	 * @return whether both values are equal
	 */
	public boolean equals(KeyModifiers other) {
		return value == other.value;
	}

}
