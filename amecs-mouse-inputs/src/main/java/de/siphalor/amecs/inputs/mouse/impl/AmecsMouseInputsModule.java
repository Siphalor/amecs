package de.siphalor.amecs.inputs.mouse.impl;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.inputs.mouse.api.AmecsMouseInputs;
import net.fabricmc.loader.api.FabricLoader;

public class AmecsMouseInputsModule {
	public static final String MOD_ID = "amecs_mouse_inputs";
	public static final boolean PRIORITY_MODULE_PRESENT = FabricLoader.getInstance().isModLoaded("amecs_priority_key_mappings");

	public static String makeKeyID(String keyName) {
		return "key." + MOD_ID + "." + keyName;
	}

	public static InputConstants.Key getKeyFromVerticalScroll(double deltaY) {
		if (deltaY == 0D) {
			return null;
		}
		return InputConstants.Type.MOUSE.getOrCreate(deltaY > 0 ? AmecsMouseInputs.SCROLL_UP : AmecsMouseInputs.SCROLL_DOWN);
	}

	//# if MC_VERSION_NUMBER >= 12002
	public static InputConstants.Key getKeyFromHorizontalScroll(double deltaX) {
		if (deltaX == 0D) {
			return null;
		}
		return InputConstants.Type.MOUSE.getOrCreate(deltaX > 0 ? AmecsMouseInputs.SCROLL_RIGHT : AmecsMouseInputs.SCROLL_LEFT);
	}
	//# end
}
