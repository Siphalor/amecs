package de.siphalor.amecstestmod;

import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.nmuk.api.NMUKAlternatives;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class AmecsTestMod implements ClientModInitializer {
	public static final String MOD_ID = "amecstestmod";

	@Override
	public void onInitializeClient() {
		KeyBinding kbd = KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(new Identifier(MOD_ID, "kbd"), InputUtil.Type.KEYSYM, 86, "key.categories.movement", new KeyModifiers(false, false, false)));
		NMUKAlternatives.create(kbd, new AmecsKeyBinding(new Identifier(MOD_ID, ""), InputUtil.Type.KEYSYM, 87, "", new KeyModifiers(true, false, false)));
	}
}
