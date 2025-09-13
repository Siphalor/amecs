package de.siphalor.amecs.compat;

import de.siphalor.nmuk.api.NMUKAlternatives;
import net.minecraft.client.KeyMapping;

public class NMUKProxy {
	public static boolean isAlternative(KeyMapping binding) {
		return NMUKAlternatives.isAlternative(binding);
	}
}
