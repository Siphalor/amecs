package de.siphalor.amecs.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.controls.ControlList;
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//# if MC_VERSION_NUMBER >= 11802
//- @Mixin(KeyBindsList.KeyEntry.class)
//# else
@Mixin(ControlList.KeyEntry.class)
//# end
public interface ControlsListWidgetKeyBindingEntryAccessor {
	@Accessor
	Button getChangeButton();
}
