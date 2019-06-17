package de.siphalor.amecs.util;

import de.siphalor.amecs.AmecsKeyBinding;
import net.minecraft.client.util.InputUtil;

public interface IKeyBinding {
	InputUtil.KeyCode getKeyCode();

	int amecs$getTimesPressed();
	void amecs$setTimesPressed(int timesPressed);

	void amecs$setPressed(boolean pressed);

	AmecsKeyBinding amecs$getAmecsKeyBinding();
	void amecs$setAmecsKeyBinding(AmecsKeyBinding amecsKeyBinding);
}
