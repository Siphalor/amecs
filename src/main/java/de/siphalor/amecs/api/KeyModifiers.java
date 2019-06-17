package de.siphalor.amecs.api;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.util.IKeyBinding;
import net.minecraft.client.options.KeyBinding;

/**
 * Defines modifiers for a key binding
 */
public class KeyModifiers {
	private char value;

	/**
	 * Flag for the alt key
	 */
	public static final char ALT = 0b1;
	/**
	 * Flag for the control key
	 */
	public static final char CONTROL = 0b10;
	/**
	 * Flag for the shift key
	 */
	public static final char SHIFT = 0b100;

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
		this((char) ((alt ? ALT : 0) | (control ? CONTROL : 0) | (shift ? SHIFT : 0)));
	}

	/**
	 * Compares this object with the current pressed keys
	 * @return whether the modifiers match in the current context
	 */
	public boolean matches() {
		return equals(Amecs.CURRENT_MODIFIERS);
	}

	/**
	 * Sets the raw value
	 * @param value the value with flags set
	 */
	public void setValue(char value) {
		this.value = value;
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
	public void setAlt(boolean value) {
		this.value = Utils.setFlag(this.value, ALT, value);
	}

	/**
	 * Gets the state of the alt flag
	 * @return whether the alt key needs to be pressed
	 */
	public boolean getAlt() {
		return Utils.getFlag(value, ALT);
	}

	/**
	 * Sets the control flag
	 * @param value whether the control flag should be activated or not
	 */
	public void setControl(boolean value) {
		this.value = Utils.setFlag(this.value, CONTROL, value);
	}

	/**
	 * Gets the state of the control flag
	 * @return whether the control key needs to be pressed
	 */
	public boolean getControl() {
		return Utils.getFlag(value, CONTROL);
	}

	/**
	 * Sets the shift flag
	 * @param value whether the shift flag should be activated or not
	 */
	public void setShift(boolean value) {
		this.value = Utils.setFlag(this.value, SHIFT, value);
	}

	/**
	 * Gets the state of the shift flag
	 * @return whether the shift key needs to be pressed
	 */
	public boolean getShift() {
		return Utils.getFlag(value, SHIFT);
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
		if(Utils.isAltKey(keyCode)) {
			value = Utils.removeFlag(value, ALT);
		} else if(Utils.isControlKey(keyCode)) {
			value = Utils.removeFlag(value, CONTROL);
		} else if(Utils.isShiftKey(keyCode)) {
			value = Utils.removeFlag(value, SHIFT);
		}
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
