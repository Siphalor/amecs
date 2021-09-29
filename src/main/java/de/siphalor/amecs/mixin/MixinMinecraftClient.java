package de.siphalor.amecs.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.siphalor.amecs.Amecs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

	// we remember if we just did a drop then we skip the drop from the single drop keybinding
	@Unique
	private boolean justDroppedStack = false;

	// we add in the dropEntireStack logic before keyDrop is checked
	@Inject(method = "handleInputEvents()V", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, shift = Shift.BY, by = -2, target = "Lnet/minecraft/client/option/GameOptions;keyDrop:Lnet/minecraft/client/option/KeyBinding;", ordinal = 0))
	private void addIn_dropEntireStack(CallbackInfo ci) {
		justDroppedStack = Amecs.KEYBINDING_DROP_STACK.handleDropItemStackEvent((MinecraftClient) (Object) this);
	}

	@Redirect(method = "handleInputEvents()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;dropSelectedItem(Z)Z", ordinal = 0))
	public boolean dropSelectedItem(ClientPlayerEntity player, boolean entireStack) {
		boolean dropResult = false;
		if (!justDroppedStack) {
			// ensure that entireStack is always false
			dropResult = player.dropSelectedItem(false);
		}
		justDroppedStack = false;
		return dropResult;
	}

}
