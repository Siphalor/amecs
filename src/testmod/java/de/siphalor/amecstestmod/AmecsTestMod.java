package de.siphalor.amecstestmod;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.nmuk.api.NMUKAlternatives;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

public class AmecsTestMod implements ClientModInitializer {
	public static final String MOD_ID = "amecstestmod";

	@Override
	public void onInitializeClient() {
		KeyMapping kbd = KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(
				new ResourceLocation(MOD_ID, "kbd"),
				InputConstants.Type.KEYSYM,
				86,
				"key.categories.movement",
				new KeyModifiers(false, false, false)
		));
		NMUKAlternatives.create(kbd, new AmecsKeyBinding(
				new ResourceLocation(MOD_ID, "test"),
				InputConstants.Type.KEYSYM,
				87,
				"",
				new KeyModifiers(true, false, false)
		));
	}
}
