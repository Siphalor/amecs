package de.siphalor.amecs;

import de.siphalor.amecs.util.IKeyBinding;
import de.siphalor.amecs.util.Utils;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class AmecsKeyBinding {
	private final KeyBinding keyBinding;

	private char modifiers = 0;

	public static char currentModifiers = 0;
	public static final char ALT = 0b1;
	public static final char CONTROL = 0b10;
	public static final char SHIFT = 0b100;

	public AmecsKeyBinding(KeyBinding keyBinding) {
		this.keyBinding = keyBinding;
	}

	public KeyBinding getKeyBinding() {
		return keyBinding;
	}

	public boolean matches(InputUtil.KeyCode keyCode) {
		return ((IKeyBinding) keyBinding).getKeyCode().equals(keyCode) && modifiersMatch();
	}

	public boolean modifiersMatch() {
		return modifiers == currentModifiers;
	}

	public void setModifiers(char modifiers) {
		this.modifiers = modifiers;
	}

	public char getModifiers() {
		return modifiers;
	}

	public void setAlt(boolean value) {
		modifiers = Utils.setFlag(modifiers, ALT, value);
	}

	public void setControl(boolean value){
		modifiers = Utils.setFlag(modifiers, CONTROL, value);
	}

	public void setShift(boolean value) {
		modifiers = Utils.setFlag(modifiers, SHIFT, value);
	}

	public boolean getAlt() {
		return (modifiers & ALT) != 0;
	}

	public boolean getControl() {
		return (modifiers & CONTROL) != 0;
	}

	public boolean getShift() {
		return (modifiers & SHIFT) != 0;
	}

	public void cleanupModifiers() {
        int keyCode = ((IKeyBinding) keyBinding).getKeyCode().getKeyCode();
        if(Amecs.isShiftKey(keyCode) && getShift()) {
        	setShift(false);
		} else if(Amecs.isAltKey(keyCode) && getAlt()) {
        	setAlt(false);
		} else if(Amecs.isControlKey(keyCode) && getControl()) {
        	setControl(false);
		}
	}

	public boolean equals(AmecsKeyBinding other) {
		if(other == null) return false;
		return modifiers == other.modifiers;
	}

}
