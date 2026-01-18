package de.siphalor.amecs.key_modifiers.api;

import de.siphalor.amecs.key_modifiers.impl.duck.IKeyMapping;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.KeyMapping;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AmecsKeyModifiersApi {
	/**
	 * Gets the default modifiers of the given key binding.
	 * The returned value <b>must not be modified!</b>
	 *
	 * @param mapping the key binding
	 * @return a reference to the default modifiers
	 */
	public static AmecsKeyModifierCombination getDefaultModifiers(KeyMapping mapping) {
		if (mapping instanceof AmecsKeyBindingWithKeyModifiers) {
			return ((AmecsKeyBindingWithKeyModifiers) mapping).getDefaultAmecsKeyModifiers();
		}
		return AmecsKeyModifierCombination.NO_MODIFIERS;
	}

	public static AmecsKeyModifierCombination getBoundModifiers(KeyMapping mapping) {
		return ((IKeyMapping) mapping).amecs$getBoundKeyModifiers();
	}

	public static void resetBoundModifiers(KeyMapping mapping) {
		if (mapping instanceof AmecsKeyBindingWithKeyModifiers) {
			((AmecsKeyBindingWithKeyModifiers) mapping).resetKeyBinding();
		} else {
			((IKeyMapping) mapping).amecs$getBoundKeyModifiers().unset();
		}
	}
}
