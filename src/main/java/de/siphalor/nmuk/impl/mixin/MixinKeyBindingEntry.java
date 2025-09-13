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
//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.nmuk.impl.IKeyBinding;
import de.siphalor.nmuk.impl.NMUKKeyBindingHelper;
import lombok.val;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
//- import net.minecraft.client.gui.screens.controls.ControlList;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TextComponent;
//- import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//# if MC_VERSION_NUMBER >= 11800
@Mixin(KeyBindsList.KeyEntry.class)
//# else
//- @Mixin(ControlList.KeyEntry.class)
//# end
public class MixinKeyBindingEntry {
	//# if MC_VERSION_NUMBER >= 11900
	@Unique
	private static final Component REMOVE_NAME = Component.literal("x");
	@Unique
	private static final Component ADD_NAME = Component.literal("+");
	@Unique
	private static final Component ENTRY_NAME = Component.literal("    ->");
	@Unique
	private static final Component RESET_TOOLTIP = Component.translatable("nmuk.options.controls.reset.tooltip");
	//# else
	//- @Unique
	//- private static final Component REMOVE_NAME = new TextComponent("x");
	//- @Unique
	//- private static final Component ADD_NAME = new TextComponent("+");
	//- @Unique
	//- private static final Component ENTRY_NAME = new TextComponent("    ->");
	//- @Unique
	//- private static final Component RESET_TOOLTIP = new TranslatableComponent("nmuk.options.controls.reset.tooltip");
	//# end

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
	//# if MC_VERSION_NUMBER >= 11800
	private KeyBindsList listWidget;
	//# else
	//- private ControlList listWidget;
	//# end
	@Unique
	private Button alternativesButton;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onConstruct(
			//# if MC_VERSION_NUMBER >= 11800
			KeyBindsList outer,
			//# else
			//- ControlList outer,
			//# end
			KeyMapping binding,
			Component text,
			CallbackInfo ci
	) {
		IKeyBinding iKeyBinding = (IKeyBinding) binding;
		if (iKeyBinding.nmuk_isAlternative()) {
			name = ENTRY_NAME;
			//# if MC_VERSION_NUMBER >= 11904
			alternativesButton = Button.builder(REMOVE_NAME, button -> onRemoveClicked(binding))
					.size(20, 20)
					.build();
			//# else
			//- alternativesButton = new Button(0, 0, 20, 20, REMOVE_NAME, button -> onRemoveClicked(binding));
			//# end
		} else {
			//# if MC_VERSION_NUMBER >= 11904
			alternativesButton = Button.builder(ADD_NAME, button -> onAddClicked(binding))
					.size(20, 20)
					.build();
			resetButton.setTooltip(Tooltip.create(RESET_TOOLTIP));
			//# else
			//- alternativesButton = new Button(0, 0, 20, 20, ADD_NAME,  button -> onAddClicked(binding));
			//- ((ButtonAccessor) alternativesButton).setOnTooltip((button, poseStack, x, y) ->
			//- 		Minecraft.getInstance().screen.renderTooltip(poseStack, RESET_TOOLTIP, x, y)
			//- );
			//# end
		}
	}

	@Unique
	private void onRemoveClicked(KeyMapping binding) {
		((IKeyBinding) ((IKeyBinding) binding).nmuk_getParent()).nmuk_removeAlternative(binding);
		NMUKKeyBindingHelper.removeKeyBinding(binding);
		val entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
		if (entries != null) {
			//noinspection SuspiciousMethodCalls
			entries.remove(this);
		}
	}

	@Unique
	private void onAddClicked(KeyMapping binding) {
		KeyMapping altBinding = NMUKKeyBindingHelper.createAlternativeKeyBinding(binding);
		NMUKKeyBindingHelper.registerKeyBinding(altBinding);
		val altEntry = NMUKKeyBindingHelper.createKeyBindingEntry(listWidget, altBinding, ENTRY_NAME);
		if (altEntry != null) {
			val entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
			if (entries != null) {
				for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
					// noinspection ConstantConditions
					if (entries.get(i) == (Object) this) {
						i += ((IKeyBinding) binding).nmuk_getAlternativesCount();
						entries.add(i, altEntry);
						break;
					}
				}
			}
		}
	}

	@Inject(method = "method_19870(Lnet/minecraft/client/KeyMapping;Lnet/minecraft/client/gui/components/Button;)V", at = @At("HEAD"))
	private void resetButtonPressed(KeyMapping keyBinding, Button widget, CallbackInfo ci) {
		if (((IKeyBinding) keyBinding).nmuk_getParent() == null && Screen.hasShiftDown()) {
			List<KeyMapping> alternatives = ((IKeyBinding) keyBinding).nmuk_getAlternatives();
			List<KeyMapping> defaultAlternatives = new ArrayList<>(NMUKKeyBindingHelper.defaultAlternatives.get(keyBinding));
			val entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
			// noinspection ConstantConditions,SuspiciousMethodCalls
			int entryPos = entries.indexOf((Object) this);

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

			/*# if MC_VERSION_NUMBER >= 11800 */KeyBindsList/*# else *//*- ControlList *//*# end */.KeyEntry entry;
			NMUKKeyBindingHelper.registerKeyBindings(Minecraft.getInstance().options, defaultAlternatives);
			alternatives.addAll(defaultAlternatives);
			for (KeyMapping defaultAlternative : defaultAlternatives) {
				entry = NMUKKeyBindingHelper.createKeyBindingEntry(listWidget, defaultAlternative, ENTRY_NAME);
				entries.add(++entryPos, entry);
				NMUKKeyBindingHelper.resetSingleKeyBinding(defaultAlternative);
			}
		}
	}

	//# if MC_VERSION_NUMBER >= 12005
	@ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 0), ordinal = 7)
	//# else
	//- @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 2, argsOnly = true)
	//# end
	public int adjustXPosition(int original) {
		return original - 30;
	}

	@Inject(method = "render", at = @At("RETURN"))
	public void render(
			//# if MC_VERSION_NUMBER >= 12000
			GuiGraphics context,
			//# else
			//- PoseStack context,
			//# end
			int index,
			int y,
			int x,
			int entryWidth,
			int entryHeight,
			int mouseX,
			int mouseY,
			boolean hovered,
			float tickDelta,
			CallbackInfo callbackInfo
	) {
		//# if MC_VERSION_NUMBER >= 11904
		alternativesButton.setY(resetButton.getY());
		alternativesButton.setX(resetButton.getX() + resetButton.getWidth() + 10);
		//# else
		//- alternativesButton.y = resetButton.y;
		//- alternativesButton.x = resetButton.x + resetButton.getWidth() + 10;
		//# end
		alternativesButton.render(context, mouseX, mouseY, tickDelta);
	}

	@Inject(method = "children", at = @At("RETURN"), cancellable = true)
	public void children(CallbackInfoReturnable<List<? extends GuiEventListener>> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ImmutableList.of(changeButton, resetButton, alternativesButton));
	}

	//# if MC_VERSION_NUMBER >= 11700
	@Inject(method = "narratables", at = @At("RETURN"), cancellable = true)
	public void selectableChildren(CallbackInfoReturnable<List<? extends GuiEventListener>> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ImmutableList.of(changeButton, resetButton, alternativesButton));
	}
	//# end

	//# if MC_VERSION_NUMBER <= 11903
	//- @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	//- public void mouseClicked(double x, double y, int button, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
	//- 	if (alternativesButton.mouseClicked(x, y, button)) {
	//- 		callbackInfoReturnable.setReturnValue(true);
	//- 	}
	//- }

	//- @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
	//- public void mouseReleased(double x, double y, int button, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
	//- 	if (alternativesButton.mouseReleased(x, y, button)) {
	//- 		callbackInfoReturnable.setReturnValue(true);
	//- 	}
	//- }
	//# end
}
