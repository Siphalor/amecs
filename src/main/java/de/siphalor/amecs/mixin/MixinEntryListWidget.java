package de.siphalor.amecs.mixin;

import de.siphalor.amecs.gui.SearchFieldControlsListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(EntryListWidget.class)
public abstract class MixinEntryListWidget {
	@Shadow protected abstract int addEntry(EntryListWidget.Entry<?> entry);

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onConstruct(MinecraftClient client, int width, int height, int y, int itemHeight, CallbackInfo ci) {
		if (getClass().equals(ControlsListWidget.class)) {
			this.addEntry((new SearchFieldControlsListWidget(client)));
		}
	}
}
