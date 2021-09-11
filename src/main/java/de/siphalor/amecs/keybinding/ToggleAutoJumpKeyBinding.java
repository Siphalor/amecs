package de.siphalor.amecs.keybinding;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;

public class ToggleAutoJumpKeyBinding extends AmecsKeyBinding {
	public ToggleAutoJumpKeyBinding(String id, InputUtil.Type type, int code, String category, KeyModifiers defaultModifiers) {
		super(id, type, code, category, defaultModifiers);
	}

	@Override
	public void onPressed() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.options.autoJump = !minecraftClient.options.autoJump;
		Amecs.sendToggleMessage(minecraftClient.player, minecraftClient.options.autoJump, new TranslatableText("amecs.toggled.auto_jump"));
	}
}
