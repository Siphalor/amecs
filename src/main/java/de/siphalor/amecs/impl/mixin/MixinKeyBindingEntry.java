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

import com.mojang.blaze3d.platform.InputConstants;
//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.impl.AmecsAPI;
import de.siphalor.amecs.impl.duck.IKeyBindingEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
//- import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.navigation.ScreenRectangle;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
//# else
//- import net.minecraft.client.gui.screens.controls.ControlList;
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
//# end
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
//- import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

//- import java.util.ArrayList;
//- import java.util.Arrays;
//- import java.util.List;

@SuppressWarnings("WeakerAccess")
@Environment(EnvType.CLIENT)
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
		implements IKeyBindingEntry {
	@Unique
	private static final String DESCRIPTION_SUFFIX = "." + AmecsAPI.MOD_ID + ".description";

	@Shadow
	@Final
	private KeyMapping key;
	@Shadow
	@Final
	private Button changeButton;

	@Unique
	//# if MC_VERSION_NUMBER >= 12106
	private WidgetTooltipHolder description;
	//# elif MC_VERSION_NUMBER >= 11600
	//- private List<Component> description;
	//# else
	//- private List<String> description;
	//# end

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onConstructed(
			/*# if MC_VERSION_NUMBER >= 11800 */KeyBindsList/*# else *//*- ControlList *//*# end */ parent,
			KeyMapping keyBinding,
			/*# if MC_VERSION_NUMBER >= 11600 */Component text,/*# end */
			CallbackInfo callbackInfo
	) {
		String descriptionKey = key.getName() + DESCRIPTION_SUFFIX;
		if (I18n.exists(descriptionKey)) {
			//# if MC_VERSION_NUMBER >= 12106
			description = new WidgetTooltipHolder();
			MutableComponent component = MutableComponent.create(new TranslatableContents(
					descriptionKey,
					"",
					new Object[0]
			));
			description.set(Tooltip.create(component));
			//# elif MC_VERSION_NUMBER >= 11600
			//- String[] lines = StringUtils.split(I18n.get(descriptionKey), '\n');
			//- description = new ArrayList<>(lines.length);
			//- for (String line : lines) {
			//- 	//# if MC_VERSION_NUMBER >= 11900
			//- 	description.add(Component.literal(line));
			//- 	//# else
			//- 	description.add(new TextComponent(line));
			//- 	//# end
			//- }
			//# else
			//- description = Arrays.asList(StringUtils.split(I18n.get(descriptionKey), '\n'));
			//# end
		} else {
			description = null;
		}
	}

	//# if MC_VERSION_NUMBER >= 12109
	@Inject(method = "renderContent", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void onRendered(
			GuiGraphics context,
			int mouseX,
			int mouseY,
			boolean hovered,
			float delta,
			CallbackInfo callbackInfo,
			int x,
			int y
	) {
	//# elif MC_VERSION_NUMBER >= 12000
	//- @Inject(method = "render", at = @At("RETURN"))
	//- public void onRendered(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta, CallbackInfo callbackInfo) {
	//# elif MC_VERSION_NUMBER >= 11600
	//- @Inject(method = "render", at = @At("RETURN"))
	//- public void onRendered(PoseStack poseStack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta, CallbackInfo callbackInfo) {
	//# else
	//- @Inject(method = "render", at = @At("RETURN"))
	//- public void onRendered(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta, CallbackInfo callbackInfo) {
	//# end
		if (description != null) {
			//# if MC_VERSION_NUMBER >= 12109
			description.refreshTooltipForNextRenderPass(
					context,
					mouseX,
					mouseY,
					isMouseOverTitle(y, 20, mouseX, mouseY),
					changeButton.isFocused(),
					new ScreenRectangle(x, y, changeButton.getX(), 20)
			);
			//# elif MC_VERSION_NUMBER >= 12106
			//- description.refreshTooltipForNextRenderPass(
			//- 		context,
			//- 		mouseX,
			//- 		mouseY,
			//- 		isMouseOverTitle(y, entryHeight, mouseX, mouseY),
			//- 		changeButton.isFocused(),
			//- 		new ScreenRectangle(x, y, entryWidth, entryHeight)
			//- );
			//# else
			//- if (isMouseOverTitle(y, entryHeight, mouseX, mouseY)) {
			//- 	//# if MC_VERSION_NUMBER >= 12000
			//- 	context.renderComponentTooltip(Minecraft.getInstance().font, description, mouseX, mouseY);
			//- 	//# elif MC_VERSION_NUMBER >= 11600
			//- 	Minecraft.getInstance().screen.renderComponentTooltip(poseStack, description, mouseX, mouseY);
			//- 	//# else
			//- 	Minecraft.getInstance().screen.renderTooltip(description, mouseX, mouseY);
			//- 	//# end
			//- }
			//# end
		}
	}

	@Unique
	private boolean isMouseOverTitle(int y, int entryHeight, int mouseX, int mouseY) {
		//# if MC_VERSION_NUMBER >= 11903
		return mouseY >= y && mouseY < y + entryHeight && mouseX < changeButton.getX();
		//# else
		//- return mouseY >= y && mouseY < y + entryHeight && mouseX < changeButton.x;
		//# end
	}

	@Inject(
			method = "method_19870(Lnet/minecraft/client/KeyMapping;Lnet/minecraft/client/gui/components/Button;)V",
			at = @At("HEAD")
	)
	public void onResetButtonClicked(KeyMapping keyBinding, Button buttonWidget, CallbackInfo callbackInfo) {
		KeyBindingUtils.resetBoundModifiers(keyBinding);
	}

	@Inject(method = "method_19871(Lnet/minecraft/client/KeyMapping;Lnet/minecraft/client/gui/components/Button;)V", at = @At("HEAD"))
	public void onEditButtonClicked(KeyMapping keyBinding, Button buttonWidget, CallbackInfo callbackInfo) {
		key.setKey(InputConstants.UNKNOWN);
	}

	@Override
	public KeyMapping amecs$getKeyBinding() {
		return key;
	}
}
