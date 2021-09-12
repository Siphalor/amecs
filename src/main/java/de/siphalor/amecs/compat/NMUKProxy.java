package de.siphalor.amecs.compat;

import java.util.List;

import de.siphalor.nmuk.api.NMUKAlternatives;
import net.minecraft.client.option.KeyBinding;

public class NMUKProxy {
	public static boolean isAlternative(KeyBinding binding) {
		return NMUKAlternatives.isAlternative(binding);
	}
	public static List<KeyBinding> getAlternatives(KeyBinding binding) {
		return NMUKAlternatives.getAlternatives(binding);
	}
	public static KeyBinding getBase(KeyBinding binding) {
		return NMUKAlternatives.getBase(binding);
	}
}
