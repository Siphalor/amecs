package de.siphalor.amecs.mixin;

import de.siphalor.amecs.gui.SearchFieldControlsListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.controls.ControlsListWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("WeakerAccess")
@Mixin(ElementListWidget.class)
public abstract class MixinElementListWidget extends EntryListWidget {
	public MixinElementListWidget(MinecraftClient minecraftClient_1, int int_1, int int_2, int int_3, int int_4, int int_5) {
		super(minecraftClient_1, int_1, int_2, int_3, int_4, int_5);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onConstruct(MinecraftClient minecraftClient, int int_1, int int_2, int int_3, int int_4, int int_5, CallbackInfo callbackInfo) {
		//noinspection ConstantConditions
		if((Object) this instanceof ControlsListWidget) {
			//noinspection unchecked
			this.addEntry(new SearchFieldControlsListWidget(minecraftClient));
		}
	}
}
