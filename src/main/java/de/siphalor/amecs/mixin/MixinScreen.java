package de.siphalor.amecs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import de.siphalor.amecs.gui.SearchFieldControlsListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public abstract class MixinScreen {
	
	@Redirect(method = "resize(Lnet/minecraft/client/MinecraftClient;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V"))
	public void resize(Screen screen, MinecraftClient client, int width, int height) {
		if(screen.getClass().equals(ControlsOptionsScreen.class)) {
			ControlsListWidget listWidget = ((ControlsOptionsScreenAccessor) screen).getKeyBindingListWidget();
			SearchFieldControlsListWidget searchWidget = (SearchFieldControlsListWidget) listWidget.children().get(0);
			String oldSearchText = searchWidget.textFieldWidget.getText();
			screen.init(client, width, height);
			listWidget = ((ControlsOptionsScreenAccessor) screen).getKeyBindingListWidget();
			searchWidget = (SearchFieldControlsListWidget) listWidget.children().get(0);
			searchWidget.textFieldWidget.setText(oldSearchText);
		} else {
			screen.init(client, width, height);
		}
	}
}
