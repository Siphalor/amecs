package de.siphalor.amecs.compat;

import de.siphalor.nmuk.api.NMUKAlternatives;
import net.minecraft.client.options.KeyBinding;

import java.util.List;

public class NMUKProxy {
	public static boolean isAlternative(KeyBinding binding) {
		return NMUKAlternatives.isAlternative(binding);
	}
	public static List<KeyBinding> getAlternatives(KeyBinding binding) {
		return NMUKAlternatives.getAlternatives(binding);
	}
}
