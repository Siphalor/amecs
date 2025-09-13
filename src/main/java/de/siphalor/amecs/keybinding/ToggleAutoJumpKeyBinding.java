package de.siphalor.amecs.keybinding;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import net.minecraft.client.Minecraft;
//- import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

//- import java.util.Optional;

public class ToggleAutoJumpKeyBinding extends AmecsKeyBinding {
	public ToggleAutoJumpKeyBinding(
			ResourceLocation id,
			InputConstants.Type type,
			int code,
			String category,
			KeyModifiers defaultModifiers
	) {
		super(id, type, code, category, defaultModifiers);
	}

	@Override
	public void onPressed() {
		Minecraft minecraft = Minecraft.getInstance();
		//# if MC_VERSION_NUMBER >= 11900
		//- boolean autoJump = !minecraft.options.autoJump().get();
		//- minecraft.options.autoJump().set(autoJump);
		//- Amecs.sendToggleMessage(minecraft.player, autoJump, Component.translatable("amecs.toggled.auto_jump"));
		//# else
		boolean autoJump = !minecraft.options.autoJump;
		minecraft.options.autoJump = autoJump;
		Amecs.sendToggleMessage(minecraft.player, autoJump, new TranslatableComponent("amecs.toggled.auto_jump"));
		//# end
	}
}
