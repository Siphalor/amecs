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
		MinecraftClient client = MinecraftClient.getInstance();
		client.options.autoJump = !client.options.autoJump;
		Amecs.sendToggleMessage(client.player, client.options.autoJump, new TranslatableText("amecs.toggled.auto_jump"));
	}
}
