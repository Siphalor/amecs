package de.siphalor.amecs.util;

import net.minecraft.client.options.KeyBinding;

public interface IKeyBindingEntry {
	String amecs$getBindingName();
	KeyBinding amecs$getKeyBinding();
}
