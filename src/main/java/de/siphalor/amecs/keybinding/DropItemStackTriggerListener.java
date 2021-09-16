package de.siphalor.amecs.keybinding;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public interface DropItemStackTriggerListener {

	public boolean handleDropItemStackEvent(MinecraftClient client);

}
