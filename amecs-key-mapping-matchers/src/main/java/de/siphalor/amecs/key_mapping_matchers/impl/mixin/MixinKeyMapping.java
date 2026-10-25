/*
 * Copyright 2019 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.amecs.key_mapping_matchers.impl.mixin;

import de.siphalor.amecs.key_mapping_matchers.impl.AmecsKeyMappingManager;
import de.siphalor.amecs.key_mapping_matchers.impl.duck.IKeyMapping;
import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public abstract class MixinKeyMapping implements IKeyMapping {
	@Shadow
	@Final
	private static Map<String, KeyMapping> ALL;

	@Shadow
	private InputConstants.Key key;

	@Shadow
	private int clickCount;

	//# if MC_VERSION_NUMBER < 11500
	//- @Shadow
	//- private boolean isDown;
	//# end

	//# if MC_VERSION_NUMBER >= 12109
	@Inject(
			method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILnet/minecraft/client/KeyMapping$Category;)V",
			at = @At("RETURN")
	)
	private void onConstructed(
			String id,
			InputConstants.Type type,
			int defaultCode,
			KeyMapping.Category category,
			CallbackInfo ci
	) {
		AmecsKeyMappingManager.register((KeyMapping) (Object) this);
	}
	//# else
	//- @Inject(
	//- 		method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V",
	//- 		at = @At("RETURN")
	//- )
	//- private void onConstructed(String id, InputConstants.Type type, int defaultCode, String category, CallbackInfo callbackInfo) {
	//- 	AmecsKeyMappingManager.register((KeyMapping) (Object) this);
	//- }
	//# end

	@Override
	public InputConstants.Key amecs$getBoundKey() {
		return key;
	}

	@Invoker("release")
	@Override
	public abstract void amecs$reset();

	//# if MC_VERSION_NUMBER < 11500
	//- @Override
	//- public void amecs$setDown(boolean pressed) {
	//- 	isDown = pressed;
	//- }
	//# end

	@Override
	public void amecs$incrementTimesPressed() {
		clickCount++;
	}

	@Inject(method = "click", at = @At("HEAD"), cancellable = true)
	private static void onKeyPressed(InputConstants.Key keyCode, CallbackInfo callbackInfo) {
		AmecsKeyMappingManager.onKeyPressed(keyCode);
		callbackInfo.cancel();
	}

	@Inject(method = "set", at = @At("HEAD"), cancellable = true)
	private static void setKeyPressed(InputConstants.Key keyCode, boolean pressed, CallbackInfo callbackInfo) {
		AmecsKeyMappingManager.setKeyPressed(keyCode, pressed);
		callbackInfo.cancel();
	}

	@Inject(method = "setAll", at = @At("HEAD"), cancellable = true)
	private static void updatePressedStates(CallbackInfo callbackInfo) {
		AmecsKeyMappingManager.updatePressedStates();
		callbackInfo.cancel();
	}

	@Inject(method = "resetMapping", at = @At("HEAD"), cancellable = true)
	private static void updateKeysByCode(CallbackInfo callbackInfo) {
		AmecsKeyMappingManager.updateKeysByCode();
		callbackInfo.cancel();
	}

	@Inject(method = "releaseAll", at = @At("HEAD"), cancellable = true)
	private static void unpressAll(CallbackInfo callbackInfo) {
		AmecsKeyMappingManager.unpressAll();
		callbackInfo.cancel();
	}

	@SuppressWarnings({"unused", "MissingUnique"})
	private static Map<String, KeyMapping> amecs$getIdToKeyBindingMap() {
		return ALL;
	}
}
