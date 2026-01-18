package de.siphalor.amecs.inputs.mouse.impl;

import de.siphalor.amecs.priority_key_mappings.impl.AmecsPriorityKeyMappingsModule;

import com.mojang.blaze3d.platform.InputConstants;

public class AmecsMouseInputsToPriorityProxy {
	public static boolean onPressedPriority(InputConstants.Key key) {
		return AmecsPriorityKeyMappingsModule.onPressedPriority(key);
	}

	public static boolean onReleasedPriority(InputConstants.Key key) {
		return AmecsPriorityKeyMappingsModule.onReleasedPriority(key);
	}
}
