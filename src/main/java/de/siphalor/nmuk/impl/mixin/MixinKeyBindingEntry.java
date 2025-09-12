/*
 * Copyright 2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.siphalor.nmuk.impl.mixin;

import com.google.common.collect.ImmutableList;
import de.siphalor.nmuk.impl.IKeyBinding;
import de.siphalor.nmuk.impl.NMUKKeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(KeyBindsList.KeyEntry.class)
public class MixinKeyBindingEntry {
	@Unique
	private static final Component ENTRY_NAME = Component.literal("    ->");
	@Unique
	private static final Component RESET_TOOLTIP = Component.translatable("nmuk.options.controls.reset.tooltip");

	@Shadow
	@Final
	private Button resetButton;
	@Shadow
	@Final
	private Button changeButton;
	@Mutable
	@Shadow
	@Final
	private Component name;
	// This is a synthetic field containing the outer class instance
	@Shadow(aliases = "field_2742", remap = false)
	@Final
	private KeyBindsList listWidget;
	@Unique
	private Button alternativesButton;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onConstruct(KeyBindsList outer, KeyMapping binding, Component text, CallbackInfo ci) {
		IKeyBinding iKeyBinding = (IKeyBinding) binding;
		if (iKeyBinding.nmuk_isAlternative()) {
			name = ENTRY_NAME;
			alternativesButton = Button.builder(Component.literal("x"), button -> {
				((IKeyBinding) iKeyBinding.nmuk_getParent()).nmuk_removeAlternative(binding);
				NMUKKeyBindingHelper.removeKeyBinding(binding);
				List<KeyBindsList.Entry> entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
				if (entries != null) {
					entries.remove((KeyBindsList.KeyEntry) (Object) this);
				}
			}).size(20, 20).build();
		} else {
			alternativesButton = Button.builder(Component.literal("+"), button -> {
				KeyMapping altBinding = NMUKKeyBindingHelper.createAlternativeKeyBinding(binding);
				NMUKKeyBindingHelper.registerKeyBinding(altBinding);
				KeyBindsList.KeyEntry altEntry = NMUKKeyBindingHelper.createKeyBindingEntry(outer, altBinding, Component.literal("..."));
				if (altEntry != null) {
					List<KeyBindsList.Entry> entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
					if (entries != null) {
						for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
							//noinspection ConstantConditions,RedundantCast,RedundantCast
							if (entries.get(i) == (KeyBindsList.KeyEntry) (Object) this) {
								i += ((IKeyBinding) binding).nmuk_getAlternativesCount();
								entries.add(i, altEntry);
								break;
							}
						}
					}
				}
			}).size(20, 20).build();
			resetButton.setTooltip(Tooltip.create(RESET_TOOLTIP));
		}
	}

	@Inject(method = "method_19870(Lnet/minecraft/client/KeyMapping;Lnet/minecraft/client/gui/components/Button;)V", at = @At("HEAD"))
	private void resetButtonPressed(KeyMapping keyBinding, Button widget, CallbackInfo ci) {
		if (((IKeyBinding) keyBinding).nmuk_getParent() == null && Screen.hasShiftDown()) {
			List<KeyMapping> alternatives = ((IKeyBinding) keyBinding).nmuk_getAlternatives();
			List<KeyMapping> defaultAlternatives = new ArrayList<>(NMUKKeyBindingHelper.defaultAlternatives.get(keyBinding));
			List<KeyBindsList.Entry> entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
			// noinspection ConstantConditions
			int entryPos = entries.indexOf((KeyBindsList.KeyEntry) (Object) this);

			int index;
			for (Iterator<KeyMapping> iterator = alternatives.iterator(); iterator.hasNext(); ) {
				KeyMapping alternative = iterator.next();
				index = defaultAlternatives.indexOf(alternative);
				if (index == -1) {
					entries.remove(entryPos + 1 + ((IKeyBinding) alternative).nmuk_getIndexInParent());
					iterator.remove();
					NMUKKeyBindingHelper.removeKeyBinding(alternative);
					continue;
				}
				defaultAlternatives.remove(index);
				NMUKKeyBindingHelper.resetSingleKeyBinding(alternative);
			}
			entryPos += alternatives.size();

			KeyBindsList.KeyEntry entry;
			NMUKKeyBindingHelper.registerKeyBindings(Minecraft.getInstance().options, defaultAlternatives);
			alternatives.addAll(defaultAlternatives);
			for (KeyMapping defaultAlternative : defaultAlternatives) {
				entry = NMUKKeyBindingHelper.createKeyBindingEntry(listWidget, defaultAlternative, ENTRY_NAME);
				entries.add(++entryPos, entry);
				NMUKKeyBindingHelper.resetSingleKeyBinding(defaultAlternative);
			}
		}
	}

	@ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 2, argsOnly = true)
	public int adjustXPosition(int original) {
		return original - 30;
	}

	@Inject(method = "render", at = @At("RETURN"))
	public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo callbackInfo) {
		alternativesButton.setY(resetButton.getY());
		alternativesButton.setX(resetButton.getX() + resetButton.getWidth() + 10);
		alternativesButton.render(graphics, mouseX, mouseY, tickDelta);
	}

	@Inject(method = "children", at = @At("RETURN"), cancellable = true)
	public void children(CallbackInfoReturnable<List<? extends GuiEventListener>> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ImmutableList.of(changeButton, resetButton, alternativesButton));
	}

    @Inject(method = "narratables", at = @At("RETURN"), cancellable = true)
    public void selectableChildren(CallbackInfoReturnable<List<? extends GuiEventListener>> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(ImmutableList.of(changeButton, resetButton, alternativesButton));
    }
}
