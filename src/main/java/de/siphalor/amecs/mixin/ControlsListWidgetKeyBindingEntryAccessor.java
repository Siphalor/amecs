package de.siphalor.amecs.mixin;

import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public interface ControlsListWidgetKeyBindingEntryAccessor {
	@Accessor
	ButtonWidget getEditButton();
}
