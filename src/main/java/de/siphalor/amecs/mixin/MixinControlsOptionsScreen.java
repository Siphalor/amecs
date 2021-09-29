package de.siphalor.amecs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import de.siphalor.amecs.gui.SearchFieldControlsListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;

@Environment(EnvType.CLIENT)
@Mixin(ControlsOptionsScreen.class)
public abstract class MixinControlsOptionsScreen {

	@Redirect(method = "init()V", at = @At(value = "NEW", target = "(Lnet/minecraft/client/gui/screen/option/ControlsOptionsScreen;Lnet/minecraft/client/MinecraftClient;)Lnet/minecraft/client/gui/screen/option/ControlsListWidget;"))
	public ControlsListWidget onConstruct(ControlsOptionsScreen parent, MinecraftClient client) {
		ControlsListWidget ret = new ControlsListWidget(parent, client);
		SearchFieldControlsListWidget searchEntry = new SearchFieldControlsListWidget(parent, client);
		//this is equivalent to calling: ret.addEntry(searchEntry)
		ret.children().add(0, searchEntry);
		return ret;
	}
}
