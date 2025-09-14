package de.siphalor.amecs.impl;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.duck.IKeyBinding;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.KeyMapping;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KeyBindingEditGuiHelper {
	public static void handleKeyPress(KeyMapping keyBinding, InputConstants.Key key) {
		if (keyBinding.isUnbound()) {
			keyBinding.setKey(key);
		} else {
			handleAdditionalKeyPress(keyBinding, key);
		}
	}

	public static void handleAdditionalKeyPress(KeyMapping keyBinding, InputConstants.Key nextKey) {
		InputConstants.Key mainKey = ((IKeyBinding) keyBinding).amecs$getBoundKey();
		KeyModifiers keyModifiers = ((IKeyBinding) keyBinding).amecs$getKeyModifiers();
		KeyModifier mainKeyModifier = KeyModifier.fromKey(mainKey);

		KeyModifier nextKeyModifier = KeyModifier.fromKey(nextKey);

		if (nextKeyModifier == KeyModifier.NONE) {
			if (mainKeyModifier != KeyModifier.NONE) {
				// swap'em around
				keyModifiers.set(mainKeyModifier, true);
				setKey(keyBinding, nextKey);
			} else {
				// shouldn't happen, as editing should already have been aborted
				setKey(keyBinding, nextKey);
			}
		} else {
			// add to existing modifiers
			keyModifiers.set(nextKeyModifier, true);
			if (mainKeyModifier != KeyModifier.NONE) {
				// special case, if e.g. both left and right shift are pressed:
				// we don't want to end up with Shift + Shift
				keyModifiers.set(mainKeyModifier, false);
			}
		}
	}

	private static void setKey(KeyMapping keyBinding, InputConstants.Key key) {
		//# if MC_VERSION_NUMBER >= 12102
		keyBinding.setKey(key);
		//# else
		//- Minecraft.getInstance().options.setKey(keyBinding, key);
		//# end
	}
}
