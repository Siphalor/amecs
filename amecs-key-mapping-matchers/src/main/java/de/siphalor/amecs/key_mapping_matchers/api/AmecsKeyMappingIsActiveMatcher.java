package de.siphalor.amecs.key_mapping_matchers.api;

import net.minecraft.client.KeyMapping;

@FunctionalInterface
public interface AmecsKeyMappingIsActiveMatcher {
	AmecsKeyMappingMatchResult matchIsActive(KeyMapping mapping);
}
