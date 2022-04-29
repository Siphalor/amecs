package de.siphalor.amecs.keybinding;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

public class ToggleAutoJumpKeyBinding extends AmecsKeyBinding {
	public ToggleAutoJumpKeyBinding(Identifier id, InputUtil.Type type, int code, String category, KeyModifiers defaultModifiers) {
		super(id, type, code, category, defaultModifiers);
	}

	@Override
	public void onPressed() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		boolean autoJump = !minecraftClient.options.getAutoJump().getValue();
		minecraftClient.options.getAutoJump().setValue(autoJump);
		Amecs.sendToggleMessage(minecraftClient.player, autoJump, Text.translatable("amecs.toggled.auto_jump"));
	}
}
