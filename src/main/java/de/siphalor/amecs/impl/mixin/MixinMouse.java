/*
 * Copyright 2020-2023 Siphalor
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

package de.siphalor.amecs.impl.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.AmecsAPI;
import de.siphalor.amecs.impl.KeyBindingManager;
import de.siphalor.amecs.impl.duck.IKeyBinding;
import de.siphalor.amecs.impl.duck.IMouse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
//# else
//- import net.minecraft.client.gui.screens.controls.ControlsScreen;
//- import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
//# end
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// TODO: Fix the priority when Mixin 0.8 is a thing and try again (-> MaLiLib causes incompatibilities)
@Environment(EnvType.CLIENT)
@Mixin(value = MouseHandler.class, priority = -2000)
public class MixinMouse implements IMouse {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Unique
	private boolean mouseScrolled_eventUsed;

	@Override
	public boolean amecs$getMouseScrolledEventUsed() {
		return mouseScrolled_eventUsed;
	}

	@Inject(
			//# if MC_VERSION_NUMBER >= 12109
			method = "onButton",
			//# else
			//- method = "onPress",
			//# end
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", ordinal = 0),
			cancellable = true
	)
	//# if MC_VERSION_NUMBER >= 12109
	private void onMouseButtonPriority(long window, MouseButtonInfo event, int state, CallbackInfo callbackInfo) {
		if (state == 1
				&& KeyBindingManager.onKeyPressedPriority(InputConstants.Type.MOUSE.getOrCreate(event.button()))) {
			callbackInfo.cancel();
		}
	}
	//# else
	//- private void onMouseButtonPriority(long window, int type, int state, int int_3, CallbackInfo callbackInfo) {
	//- 	if (state == 1 && KeyBindingManager.onKeyPressedPriority(InputConstants.Type.MOUSE.getOrCreate(type))) {
	//- 		callbackInfo.cancel();
	//- 	}
	//- }
	//# end

	@Unique
	//# if MC_VERSION_NUMBER >= 12002
	private void onScrollReceived(double scrollAmountX, double scrollAmountY) {
		InputConstants.Key keyCodeX = KeyBindingUtils.getKeyFromHorizontalScroll(scrollAmountX);
		if (keyCodeX != null) {
			handleScrollKey(keyCodeX, scrollAmountX);
		}
	//# else
	//- private void onScrollReceived(double scrollAmountY) {
	//# end
		InputConstants.Key keyCodeY = KeyBindingUtils.getKeyFromVerticalScroll(scrollAmountY);
		if (keyCodeY != null) {
			handleScrollKey(keyCodeY, scrollAmountY);
		}
	}

	@Unique
	private void handleScrollKey(@NotNull InputConstants.Key key, double amount) {
		KeyMapping.set(key, true);

		amount = Math.abs(amount);
		while (amount > 0) {
			KeyMapping.click(key);
			amount--;
		}

		KeyMapping.set(key, false);
	}

	@SuppressWarnings("InvalidInjectorMethodSignature")
	@Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	//# if MC_VERSION_NUMBER >= 12002
	private void isSpectator_onMouseScroll(long window, double rawX, double rawY, CallbackInfo callbackInfo, boolean discreteScroll, double sensitivity, double scrollAmountX, double scrollAmountY) {
	//# else
	//- private void isSpectator_onMouseScroll(long window, double rawX, double rawY, CallbackInfo callbackInfo, double scrollAmountY) {
	//# end
		if (AmecsAPI.TRIGGER_KEYBINDING_ON_SCROLL) {
			//# if MC_VERSION_NUMBER >= 12002
			this.onScrollReceived(scrollAmountX, scrollAmountY);
			//# else
			//- this.onScrollReceived(scrollAmountY);
			//# end
		}
	}

	//# if MC_VERSION_NUMBER >= 12002
	@WrapOperation(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDDD)Z"))
	private boolean onMouseScrolledScreen(Screen screen, double mouseX, double mouseY, double xScrollAmount, double yScrollAmount, Operation<Boolean> original) {
		Boolean handled = original.call(screen, mouseX, mouseY, xScrollAmount, yScrollAmount);
		return onMouseScrolledScreen(handled, xScrollAmount, yScrollAmount);
	}
	//# else
	//- @WrapOperation(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDD)Z"))
	//- private boolean onMouseScrolledScreen(Screen screen, double mouseX, double mouseY, double yScrollAmount, Operation<Boolean> original) {
	//- 	Boolean handled = original.call(screen, mouseX, mouseY, yScrollAmount);
	//- 	return amecs$onMouseScrolledScreen(handled, yScrollAmount);
	//- }
	//# end

	@Unique
	private boolean onMouseScrolledScreen(
			boolean handled,
			//# if MC_VERSION_NUMBER >= 12002
			double xScrollAmount,
			//# end
			double yScrollAmount
	) {
		this.mouseScrolled_eventUsed = handled;
		if (handled) {
			return true;
		}

		if (AmecsAPI.TRIGGER_KEYBINDING_ON_SCROLL) {
			//# if MC_VERSION_NUMBER >= 12002
			this.onScrollReceived(xScrollAmount, yScrollAmount);
			//# else
			//- this.onScrollReceived(yScrollAmount);
			//# end
		}
		return false;
	}

	@Inject(method = "onScroll", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	//# if MC_VERSION_NUMBER >= 12002
	private void onMouseScroll(long window, double rawX, double rawY, CallbackInfo callbackInfo, boolean discreteScroll, double sensitivity, double scrollAmountX, double scrollAmountY) {
	//# else
	//- private void onMouseScroll(long window, double rawX, double rawY, CallbackInfo callbackInfo, double scrollAmountY) {
	//# end
		InputConstants.Key keyCodeY = KeyBindingUtils.getKeyFromVerticalScroll(scrollAmountY);
		//# if MC_VERSION_NUMBER >= 12002
		InputConstants.Key keyCodeX = KeyBindingUtils.getKeyFromHorizontalScroll(scrollAmountX);
		InputConstants.Key primaryKeyCode = keyCodeY != null ? keyCodeY : keyCodeX;
		//# else
		//- InputConstants.Key primaryKeyCode = keyCodeY;
		//# end

		// check if we have scroll input for the options screen
		//# if MC_VERSION_NUMBER >= 11800
		if (minecraft.screen instanceof KeyBindsScreen) {
		//# else
		//- if (minecraft.screen instanceof ControlsScreen) {
		//# end
			if (handleScrollInKeybindsScreen(callbackInfo, primaryKeyCode)) return;
		}

		// Legacy support
		//noinspection deprecation
		KeyBindingUtils.setLastScrollAmount(scrollAmountY);
		if (KeyBindingManager.onKeyPressedPriority(keyCodeY)) {
			callbackInfo.cancel();
		}
		//# if MC_VERSION_NUMBER >= 12002
		if (KeyBindingManager.onKeyPressedPriority(keyCodeX)) {
			callbackInfo.cancel();
		}
		//# end
	}

	@Unique
	private boolean handleScrollInKeybindsScreen(CallbackInfo callbackInfo, InputConstants.Key primaryKeyCode) {
		assert minecraft.screen != null;
		//# if MC_VERSION_NUMBER >= 11800
		KeyMapping focusedBinding = ((KeyBindsScreen) minecraft.screen).selectedKey;
		//# else
		//- KeyMapping focusedBinding = ((ControlsScreen) minecraft.screen).selectedKey;
		//# end
		if (focusedBinding != null) {
			if (!focusedBinding.isUnbound()) {
				KeyModifiers keyModifiers = ((IKeyBinding) focusedBinding).amecs$getKeyModifiers();
				keyModifiers.set(KeyModifier.fromKey(((IKeyBinding) focusedBinding).amecs$getBoundKey()), true);
			}
			// This is a bit hacky, but the easiest way out
			// If the selected binding != null, the mouse x and y will always be ignored - so no need to convert them
			//# if MC_VERSION_NUMBER >= 12109
			minecraft.screen.mouseClicked(
					new MouseButtonEvent(0D, 0D, new MouseButtonInfo(primaryKeyCode.getValue(), 0)),
					true
			);
			//# else
			//- minecraft.screen.mouseClicked(-1, -1, primaryKeyCode.getValue());
			//# end
			// if we do we cancel the method because we do not want the current screen to get the scroll event
			callbackInfo.cancel();
			return true;
		}
		return false;
	}
}
