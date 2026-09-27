package de.siphalor.amecs.key_mapping_matchers.impl.duck;

import com.mojang.blaze3d.platform.InputConstants;

public interface IKeyMapping {
	InputConstants.Key amecs$getBoundKey();

	void amecs$reset();

	//# if MC_VERSION_NUMBER < 11500
	//- void amecs$setDown(boolean pressed);
	//# end

	void amecs$incrementTimesPressed();
}
