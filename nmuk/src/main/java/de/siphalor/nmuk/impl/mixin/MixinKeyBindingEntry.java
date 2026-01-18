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
//- import com.mojang.blaze3d.platform.GlStateManager;
//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.nmuk.impl.IKeyBinding;
import de.siphalor.nmuk.impl.NMUKKeyBindingHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.val;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
//- import net.minecraft.client.gui.screens.Screen;
//- import net.minecraft.client.gui.screens.controls.ControlList;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
//# else
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
//# end
//- import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TextComponent;
//- import net.minecraft.network.chat.TranslatableComponent;

//# if MC_VERSION_NUMBER >= 11800
@Mixin(KeyBindsList.KeyEntry.class)
//# else
//- @Mixin(ControlList.KeyEntry.class)
//# end

public abstract class MixinKeyBindingEntry
		//# if MC_VERSION_NUMBER >= 11800
		extends KeyBindsList.Entry
		//# else
		//- extends ControlList.Entry
		//# end
		{
	@Unique
	private static final String REMOVE_LITERAL = "x";
	@Unique
	private static final String ADD_LITERAL = "+";
	@Unique
	private static final String ALTERNATIVE_ENTRY_LITERAL = "    ->";
	@Unique
	private static final String RESET_TOOLTIP_KEY = "nmuk.options.controls.reset.tooltip";

	//# if MC_VERSION_NUMBER >= 11900
	@Unique
	private static final Component REMOVE_COMPONENT = Component.literal(REMOVE_LITERAL);
	@Unique
	private static final Component ADD_COMPONENT = Component.literal(ADD_LITERAL);
	@Unique
	private static final Component ALTERNATIVE_ENTRY_COMPONENT = Component.literal(ALTERNATIVE_ENTRY_LITERAL);
	@Unique
	private static final Component RESET_TOOLTIP_COMPONENT = Component.translatable(RESET_TOOLTIP_KEY);
	//# elif MC_VERSION_NUMBER >= 11600
	//- @Unique
	//- private static final Component REMOVE_COMPONENT = new TextComponent(REMOVE_LITERAL);
	//- @Unique
	//- private static final Component ADD_COMPONENT = new TextComponent(ADD_LITERAL);
	//- @Unique
	//- private static final Component ALTERNATIVE_ENTRY_COMPONENT = new TextComponent(ALTERNATIVE_ENTRY_LITERAL);
	//- @Unique
	//- private static final Component RESET_TOOLTIP_COMPONENT = new TranslatableComponent(RESET_TOOLTIP_KEY);
	//# else
	//- @Unique
	//- private static final String RESET_TOOLTIP = I18n.get(RESET_TOOLTIP_KEY);
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
	//# if MC_VERSION_NUMBER >= 11600
	private Component name;
	//# else
	//- private String name;
	//# end
	// This is a synthetic field containing the outer class instance
	@Shadow(aliases = "field_2742", remap = false)
	@Final
	//# if MC_VERSION_NUMBER >= 11800
	private KeyBindsList listWidget;
	//# else
	//- private ControlList listWidget;
	//- 		@Shadow
	//- 		@Final
	//- 		private KeyMapping key;
	//- 		//# end
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
			//# if MC_VERSION_NUMBER >= 11600
			Component text,
			//# end
			CallbackInfo ci
	) {
		IKeyBinding iKeyBinding = (IKeyBinding) binding;
		if (iKeyBinding.nmuk_isAlternative()) {
			//# if MC_VERSION_NUMBER >= 11600
			name = ALTERNATIVE_ENTRY_COMPONENT;
			//# else
			//- name = ALTERNATIVE_ENTRY_LITERAL;
			//# end

			//# if MC_VERSION_NUMBER >= 11903
			alternativesButton = Button.builder(REMOVE_COMPONENT, button -> onRemoveClicked(binding))
					.size(20, 20)
					.build();
			//# elif MC_VERSION_NUMBER >= 11600
			//- alternativesButton = new Button(0, 0, 20, 20, REMOVE_COMPONENT, button -> onRemoveClicked(binding));
			//# else
			//- alternativesButton = new Button(0, 0, 20, 20, REMOVE_LITERAL, button -> onRemoveClicked(binding));
			//# end
		} else {
			//# if MC_VERSION_NUMBER >= 11903
			alternativesButton = Button.builder(ADD_COMPONENT, button -> onAddClicked(binding))
					.size(20, 20)
					.build();
			resetButton.setTooltip(Tooltip.create(RESET_TOOLTIP_COMPONENT));
			//# elif MC_VERSION_NUMBER >= 11600
			//- alternativesButton = new Button(0, 0, 20, 20, ADD_COMPONENT,  button -> onAddClicked(binding));
			//- ((ButtonAccessor) resetButton).setOnTooltip((button, poseStack, x, y) ->
			//- 		Minecraft.getInstance().screen.renderTooltip(poseStack, RESET_TOOLTIP_COMPONENT, x, y)
			//- );
			//# else
			//- alternativesButton = new Button(0, 0, 20, 20, ADD_LITERAL, button -> onAddClicked(binding));
			//- // Tooltip rendering happens manually in the render injection
			//# end
		}
	}

	@Unique
	private void onRemoveClicked(KeyMapping binding) {
		((IKeyBinding) ((IKeyBinding) binding).nmuk_getParent()).nmuk_removeAlternative(binding);
		NMUKKeyBindingHelper.removeKeyBinding(binding);
		//# if MC_VERSION_NUMBER >= 12109
		listWidget.removeEntry(this);
		listWidget.refreshEntries();
		//# else
		//- listWidget.removeEntry(this);
		//# end
	}

	@Unique
	private void onAddClicked(KeyMapping binding) {
		KeyMapping altBinding = NMUKKeyBindingHelper.createAlternativeKeyBinding(binding);
		NMUKKeyBindingHelper.registerKeyBinding(altBinding);
		val altEntry = NMUKKeyBindingHelper.createKeyBindingEntry(
				listWidget,
				altBinding
				//# if MC_VERSION_NUMBER >= 11600
				, ALTERNATIVE_ENTRY_COMPONENT
				//# end
		);
		if (altEntry != null) {
			//# if MC_VERSION_NUMBER >= 12109
			val entries = new ArrayList<>(NMUKKeyBindingHelper.getControlsListWidgetEntries());
			//# else
			//- val entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
			//# end
			for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
				// noinspection ConstantConditions
				if (entries.get(i) == this) {
					i += ((IKeyBinding) binding).nmuk_getAlternativesCount();
					entries.add(i, altEntry);
					break;
				}
			}
			//# if MC_VERSION_NUMBER >= 12109
			listWidget.replaceEntries(entries);
			listWidget.refreshEntries();
			//# end
		}
	}

	@Inject(method = "method_19870(Lnet/minecraft/client/KeyMapping;Lnet/minecraft/client/gui/components/Button;)V", at = @At("HEAD"))
	private void resetButtonPressed(KeyMapping keyBinding, Button widget, CallbackInfo ci) {
		//# if MC_VERSION_NUMBER >= 12109
		if (((IKeyBinding) keyBinding).nmuk_getParent() == null && Minecraft.getInstance().hasShiftDown()) {
		//# else
		//- if (((IKeyBinding) keyBinding).nmuk_getParent() == null && Screen.hasShiftDown()) {
		//# end
			List<KeyMapping> alternatives = ((IKeyBinding) keyBinding).nmuk_getAlternatives();
			List<KeyMapping> defaultAlternatives = new ArrayList<>(NMUKKeyBindingHelper.defaultAlternatives.get(keyBinding));
			//# if MC_VERSION_NUMBER >= 12109
			val entries = new ArrayList<>(NMUKKeyBindingHelper.getControlsListWidgetEntries());
			//# else
			//- val entries = NMUKKeyBindingHelper.getControlsListWidgetEntries();
			//# end
			// noinspection ConstantConditions
			int entryPos = entries.indexOf(this);

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
				entry = NMUKKeyBindingHelper.createKeyBindingEntry(
						listWidget,
						defaultAlternative
						//# if MC_VERSION_NUMBER >= 11600
						, ALTERNATIVE_ENTRY_COMPONENT
						//# end
				);
				entries.add(++entryPos, entry);
				NMUKKeyBindingHelper.resetSingleKeyBinding(defaultAlternative);
			}

			//# if MC_VERSION_NUMBER >= 12109
			listWidget.replaceEntries(entries);
			//# end
		}
	}

	//# if MC_VERSION_NUMBER >= 12109
	@ModifyVariable(method = "renderContent", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
	//# elif MC_VERSION_NUMBER >= 12005
	//- @ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 0), ordinal = 7)
	//# else
	//- @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 2, argsOnly = true)
	//# end
	public int adjustXPosition(int original) {
		return original - 30;
	}

	//# if MC_VERSION_NUMBER >= 12109
	@Inject(method = "renderContent", at = @At("RETURN"))
	//# else
	//- @Inject(method = "render", at = @At("RETURN"))
	//# end
	public void render(
			//# if MC_VERSION_NUMBER >= 12000
			GuiGraphics context,
			//# elif MC_VERSION_NUMBER >= 11600
			//- PoseStack context,
			//# end
			//# if MC_VERSION_NUMBER < 12109
			//- int index,
			//- int y,
			//- int x,
			//- int entryWidth,
			//- int entryHeight,
			//# end
			int mouseX,
			int mouseY,
			boolean hovered,
			float tickDelta,
			CallbackInfo callbackInfo
	) {
		//# if MC_VERSION_NUMBER >= 11903
		alternativesButton.setY(resetButton.getY());
		alternativesButton.setX(resetButton.getX() + resetButton.getWidth() + 10);
		//# else
		//- alternativesButton.y = resetButton.y;
		//- alternativesButton.x = resetButton.x + resetButton.getWidth() + 10;
		//# end
		//# if MC_VERSION_NUMBER >= 11600
		alternativesButton.render(context, mouseX, mouseY, tickDelta);
		//# else
		//- alternativesButton.render(mouseX, mouseY, tickDelta);
		//# end

		//# if MC_VERSION_NUMBER < 11600
		//- if (resetButton.isMouseOver(mouseX, mouseY)
		//- 		&& resetButton.active
		//- 		&& !((IKeyBinding) key).nmuk_isAlternative()) {
		//- 	Minecraft.getInstance().screen.renderTooltip(RESET_TOOLTIP, mouseX, mouseY);
		//- 	GlStateManager.disableLighting();
		//- }
		//# end
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
