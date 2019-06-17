package de.siphalor.amecs.mixin;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.util.IKeyBinding;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@Mixin(GameOptions.class)
public class MixinGameOptions {

	@Shadow @Final public KeyBinding keyAdvancements;

	@Inject(
		method = "write",
		at = @At(value = "INVOKE", target = "Ljava/io/PrintWriter;println(Ljava/lang/String;)V", ordinal = 0),
		slice = @Slice(
			from = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;keysAll:[Lnet/minecraft/client/options/KeyBinding;")
		),
		locals = LocalCapture.CAPTURE_FAILSOFT
	)
	public void onKeyBindingWritten(CallbackInfo callbackInfo, PrintWriter printWriter, KeyBinding[] keyBindings, int keyBindingsCount, int index, KeyBinding keyBinding) {
		printWriter.println(Amecs.KEY_MODIFIER_GAME_OPTION + keyBinding.getId() + ":" + (int) ((IKeyBinding) keyBinding).amecs$getKeyModifiers().getValue());
	}

	@Inject(
		method = "load",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;keysAll:[Lnet/minecraft/client/options/KeyBinding;", shift = At.Shift.BEFORE),
		locals = LocalCapture.CAPTURE_FAILSOFT
	)
	public void onLoad(CallbackInfo callbackInfo, List list, CompoundTag compoundTag, Iterator iterator, String key, String value) {
        if(key.startsWith(Amecs.KEY_MODIFIER_GAME_OPTION)) {
			key = key.substring(Amecs.KEY_MODIFIER_GAME_OPTION.length());
			KeyBinding keyBinding = Amecs.getIdToKeyBindingMap().get(key);
			if(keyBinding != null) {
				((IKeyBinding) keyBinding).amecs$getKeyModifiers().setValue((char) Integer.parseInt(value));
			}
		}
	}
}
