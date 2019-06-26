package de.siphalor.amecs.api;

@SuppressWarnings("WeakerAccess")
public class Utils {
	/**
	 * Sets a flag to a value
	 * @param base the base value
	 * @param flag the flag to set/unset
	 * @param val whether the flag gets set/unset
	 * @return the new value
	 */
	public static char setFlag(char base, char flag, boolean val) {
		return val ? setFlag(base, flag) : removeFlag(base, flag);
	}

	/**
	 * Sets a flag on a value
	 * @param base the base value
	 * @param flag the flag to set
	 * @return the new value
	 */
	public static char setFlag(char base, char flag) {
		return (char) (base | flag);
	}

	/**
	 * Unsets a flag on a value
	 * @param base the base value
	 * @param flag the flag to unset|remove
	 * @return the new value
	 */
	public static char removeFlag(char base, char flag) {
		return (char) (base & (~flag));
	}

	/**
	 * Gets the state of a flag
	 * @param base the base value
	 * @param flag the flag to evaluate
	 * @return whether the flag is set on the base value
	 */
	public static boolean getFlag(char base, char flag) {
		return (base & flag) != 0;
	}

	/**
	 * Decides whether a key code belongs to a shift key
	 * @param keyCode the key code
	 * @return whether it's a shift key
	 * @deprecated Use {@link KeyModifier#matches(int)} ()} instead
	 */
	@Deprecated
	public static boolean isShiftKey(int keyCode) {
		return keyCode == 340 || keyCode == 344;
	}

	/**
	 * Decides whether a key code belongs to a control key
	 * @param keyCode the key code
	 * @return whether it's a control key
	 * @deprecated Use {@link KeyModifier#matches(int)} instead
	 */
	@Deprecated
	public static boolean isControlKey(int keyCode) {
		return keyCode == 341 || keyCode == 345;
	}

	/**
	 * Decides whether a key code belongs to an alt key
	 * @param keyCode the key code
	 * @return whether it's an alt key
	 * @deprecated Use {@link KeyModifier#matches(int)} instead
	 */
	@Deprecated
	public static boolean isAltKey(int keyCode) {
		return keyCode == 342 || keyCode == 346;
	}

	/**
	 * Decides whether a key code is a valid modifier key
	 * @param keyCode the key code
	 * @return whether it's a modifier key
	 * @deprecated Use {@link KeyModifier#fromKeyCode(int)} instead
	 */
	@Deprecated
	public static boolean isModifier(int keyCode) {
		return isShiftKey(keyCode) || isControlKey(keyCode) || isAltKey(keyCode);
	}
}
