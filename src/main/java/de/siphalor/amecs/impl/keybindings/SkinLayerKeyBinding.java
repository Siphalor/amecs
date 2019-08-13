package de.siphalor.amecs.impl.keybindings;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.api.ListeningKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class SkinLayerKeyBinding extends AmecsKeyBinding implements ListeningKeyBinding {
	private final PlayerModelPart playerModelPart;

	public SkinLayerKeyBinding(Identifier id, InputUtil.Type type, int code, String category, PlayerModelPart playerModelPart) {
		super(id, type, code, category, new KeyModifiers());
		this.playerModelPart = playerModelPart;
	}

	@Override
	public void onPressed() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.options.togglePlayerModelPart(playerModelPart);
		Amecs.sendToggleMessage(minecraftClient.player, minecraftClient.options.getEnabledPlayerModelParts().contains(playerModelPart), playerModelPart.getOptionName());
	}
}
