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
