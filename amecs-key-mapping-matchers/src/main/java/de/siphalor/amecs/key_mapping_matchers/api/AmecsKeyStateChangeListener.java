package de.siphalor.amecs.key_mapping_matchers.api;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.blaze3d.platform.InputConstants;

@FunctionalInterface
@ApiStatus.Experimental
public interface AmecsKeyStateChangeListener {
	void onChange(InputConstants.Key key, boolean pressed);
}
