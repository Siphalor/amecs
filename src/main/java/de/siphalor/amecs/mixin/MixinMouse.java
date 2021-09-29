package de.siphalor.amecs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerInventory;

// set very high "priority"" because we want to do it late after all other mods did their thing
@Environment(EnvType.CLIENT)
@Mixin(value = Mouse.class, priority = 2000)
public abstract class MixinMouse {

	// we redirect the following methods to do nothing because we made a keybinding for this and we do not want that these actions are done double

	@Redirect(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;cycleSlot(D)V"))
	private void redirect_cycleSlot(SpectatorHud hud, double offset) {
		// do nothing
	}

	@Redirect(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;setFlySpeed(F)V"))
	private void redirect_setFlySpeed(PlayerAbilities abilities, float flySpeed) {
		// do nothing
	}

	@Redirect(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"))
	private void redirect_scrollInHotbar(PlayerInventory inventory, double scrollAmount) {
		// do nothing
	}

}
