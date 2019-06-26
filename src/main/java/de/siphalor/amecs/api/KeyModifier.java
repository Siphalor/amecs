package de.siphalor.amecs.api;

import org.apache.commons.lang3.ArrayUtils;

@SuppressWarnings("WeakerAccess")
public class KeyModifier {
	public static final KeyModifier NONE = new KeyModifier((char) 0);
	public static final KeyModifier ALT = new KeyModifier((char) 0b1, 342, 346);
	public static final KeyModifier CONTROL = new KeyModifier((char) 0b10, 341, 345);
	public static final KeyModifier SHIFT = new KeyModifier((char) 0b100, 340, 344);

	public final char flag;
	final int[] keyCodes;

	private KeyModifier(char flag, int... keyCodes) {
		this.flag = flag;
		this.keyCodes = keyCodes;
	}

	public static KeyModifier fromKeyCode(int keyCode) {
		if(ALT.matches(keyCode)) return ALT;
		if(CONTROL.matches(keyCode)) return CONTROL;
		if(SHIFT.matches(keyCode)) return SHIFT;
		return NONE;
	}

	public boolean matches(int keyCode) {
		return ArrayUtils.contains(keyCodes, keyCode);
	}
}
