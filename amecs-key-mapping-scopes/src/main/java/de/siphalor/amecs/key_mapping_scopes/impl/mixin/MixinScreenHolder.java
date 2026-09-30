package de.siphalor.amecs.key_mapping_scopes.impl.mixin;

import de.siphalor.amecs.key_mapping_scopes.api.AmecsKeyMappingScope;
import de.siphalor.amecs.key_mapping_scopes.impl.AmecsKeyMappingScopesModule;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//- import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

//# if MC_VERSION_NUMBER >= 260200
@Mixin(Gui.class)
//# else
//- @Mixin(Minecraft.class)
//# end

public class MixinScreenHolder {
	@Inject(method = "setScreen", at = @At("RETURN"))
	public void onScreen(@Nullable Screen screen) {
		if (screen instanceof AbstractContainerScreen<?>) {
			AmecsKeyMappingScopesModule.currentScope = AmecsKeyMappingScope.INVENTORY;
		} else if (screen != null) {
			AmecsKeyMappingScopesModule.currentScope = AmecsKeyMappingScope.MENU;
		} else {
			AmecsKeyMappingScopesModule.currentScope = AmecsKeyMappingScope.IN_GAME;
		}
	}
}
