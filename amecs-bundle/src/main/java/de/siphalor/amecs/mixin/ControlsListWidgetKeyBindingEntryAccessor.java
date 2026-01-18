package de.siphalor.amecs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.Button;
//- import net.minecraft.client.gui.screens.controls.ControlList;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
//# else
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
//# end

//# if MC_VERSION_NUMBER >= 11802
@Mixin(KeyBindsList.KeyEntry.class)
//# else
//- @Mixin(ControlList.KeyEntry.class)
//# end

public interface ControlsListWidgetKeyBindingEntryAccessor {
	@Accessor
	Button getChangeButton();
	//# if MC_VERSION_NUMBER >= 12109
	@Accessor
	boolean getHasCollision();
	//# end
}
