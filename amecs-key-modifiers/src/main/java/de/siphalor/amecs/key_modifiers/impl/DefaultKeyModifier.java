package de.siphalor.amecs.key_modifiers.impl;

import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifier;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

public class DefaultKeyModifier extends AmecsKeyModifier {
	@Getter
	private final @Nullable Integer legacyId;
	private final int[] keyCodes;

	public DefaultKeyModifier(String name, @Nullable Integer legacyId, int... keyCodes) {
		super(name);
		this.legacyId = legacyId;
		this.keyCodes = keyCodes;
	}

	@Override
	public boolean matches(int keyCode) {
		return ArrayUtils.contains(keyCodes, keyCode);
	}
}
