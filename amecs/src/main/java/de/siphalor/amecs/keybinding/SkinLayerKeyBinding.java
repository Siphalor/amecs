package de.siphalor.amecs.keybinding;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;

public class SkinLayerKeyBinding extends AmecsKeyBinding {
	private final PlayerModelPart playerModelPart;

	public SkinLayerKeyBinding(
			ResourceLocation id,
			InputConstants.Type type,
			int code,
			//# if MC_VERSION_NUMBER >= 12109
			Category category,
			//# else
			//- String category,
			//# end
			PlayerModelPart playerModelPart
	) {
		super(id, type, code, category, new KeyModifiers());
		this.playerModelPart = playerModelPart;
	}

	@Override
	public void onPressed() {
		Minecraft client = Minecraft.getInstance();
		//# if MC_VERSION_NUMBER >= 12102
		client.options.setModelPart(playerModelPart, !client.options.isModelPartEnabled(playerModelPart));
		Amecs.sendToggleMessage(client.player, client.options.isModelPartEnabled(playerModelPart), playerModelPart.getName());
		//# elif MC_VERSION_NUMBER >= 11700
		//- client.options.toggleModelPart(playerModelPart, !client.options.isModelPartEnabled(playerModelPart));
		//- Amecs.sendToggleMessage(client.player, client.options.isModelPartEnabled(playerModelPart), playerModelPart.getName());
		//# else
		//- client.options.toggleModelPart(playerModelPart);
		//- Amecs.sendToggleMessage(client.player, client.options.getModelParts().contains(playerModelPart), playerModelPart.getName());
		//# end
	}
}
