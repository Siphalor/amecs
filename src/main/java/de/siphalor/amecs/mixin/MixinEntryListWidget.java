package de.siphalor.amecs.mixin;

import de.siphalor.amecs.gui.SearchFieldControlsListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.controls.ControlList;
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractSelectionList.class)
public abstract class MixinEntryListWidget {
	@Shadow protected abstract int addEntry(AbstractSelectionList.Entry<?> entry);

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onConstruct(Minecraft client, int width, int height, int y0, int y1, int itemHeight, CallbackInfo ci) {
		//# if MC_VERSION_NUMBER >= 11802
		//- //noinspection ConstantValue,EqualsBetweenInconvertibleTypes
		//- if (getClass().equals(KeyBindsList.class)) {
		//- 	this.addEntry(new SearchFieldControlsListWidget((KeyBindsList) (Object) this, client));
		//- }
		//# else
		//noinspection ConstantValue,EqualsBetweenInconvertibleTypes
		if (getClass().equals(ControlList.class)) {
			this.addEntry(new SearchFieldControlsListWidget((ControlList) (Object) this, client));
		}
		//# end
	}
}
