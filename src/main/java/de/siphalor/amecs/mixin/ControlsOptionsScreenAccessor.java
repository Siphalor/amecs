package de.siphalor.amecs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;

@Mixin(ControlsOptionsScreen.class)
public interface ControlsOptionsScreenAccessor {
	@Accessor
	ControlsListWidget getKeyBindingListWidget();
}
