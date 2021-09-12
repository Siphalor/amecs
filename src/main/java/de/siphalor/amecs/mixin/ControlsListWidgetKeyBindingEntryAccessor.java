package de.siphalor.amecs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public interface ControlsListWidgetKeyBindingEntryAccessor {
	@Accessor
	ButtonWidget getEditButton();
	
	@Accessor
	Text getBindingName();
}
