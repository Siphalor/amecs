/*
 * Copyright 2026 Siphalor
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

package de.siphalor.amecs;

import de.siphalor.amecs.key_modifiers.api.AmecsKeyMappingWithKeyModifiers;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
import de.siphalor.amecs.key_modifiers.impl.duck.IKeyBindingEntry;
import de.siphalor.amecs.keybinding.SkinLayerKeyMapping;
import de.siphalor.amecs.keybinding.ToggleAutoJumpKeyMapping;
import de.siphalor.amecs.mixin.ControlsListWidgetKeyBindingEntryAccessor;
import java.util.Arrays;
import java.util.Locale;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.InputConstants;
//- import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
//- import net.minecraft.client.gui.screens.controls.ControlList;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
//# else
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
//# end
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TextColor;
//- import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;

/**
 * Main class of Amecs (Alt-Meta-Escape-Control-Shift)
 */
@Environment(EnvType.CLIENT)
public class Amecs implements ClientModInitializer {
    /**
     * The mod id of Amecs
     */
    @SuppressWarnings("WeakerAccess")
    public static final String MOD_ID = "amecs";
    public static final String MOD_NAME_SHORT = "Amecs";

    private static final String LOGGER_PREFIX = "[" + MOD_NAME_SHORT + "] ";
    private static final Logger LOGGER = LogManager.getLogger();

	//# if MC_VERSION_NUMBER >= 12109
	private static final KeyMapping.Category SKIN_LAYER_CATEGORY = KeyMapping.Category.register(
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "skin_layers")
	);
	//# else
	//- private static final String SKIN_LAYER_CATEGORY = "key.category." + MOD_ID + ".skin_layers";
	//# end

	public static final KeyMapping ESCAPE_KEYBINDING = KeyBindingHelper.registerKeyBinding(new AmecsKeyMappingWithKeyModifiers(
			//# if MC_VERSION_NUMBER >= 12100
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "alternative_escape"),
			//# else
			//- new ResourceLocation(MOD_ID, "alternative_escape"),
			//# end
			InputConstants.Type.KEYSYM,
			-1,
			//# if MC_VERSION_NUMBER >= 12109
			KeyMapping.Category.MISC,
			//# else
			//- "key.categories.ui",
			//# end
			new AmecsKeyModifierCombination()
	));

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(new ToggleAutoJumpKeyMapping(
				//# if MC_VERSION_NUMBER >= 12100
				ResourceLocation.fromNamespaceAndPath(MOD_ID, "toggle_auto_jump"),
				//# else
				//- new ResourceLocation(MOD_ID, "toggle_auto_jump"),
				//# end
				InputConstants.Type.KEYSYM,
				66,
				//# if MC_VERSION_NUMBER >= 12109
				KeyMapping.Category.MOVEMENT,
				//# else
				//- "key.categories.movement",
				//# end
				new AmecsKeyModifierCombination()
		));

        Arrays.stream(PlayerModelPart.values())
                .map(playerModelPart -> new SkinLayerKeyMapping(
						//# if MC_VERSION_NUMBER >= 12100
						ResourceLocation.fromNamespaceAndPath(MOD_ID, "toggle_" + playerModelPart.getId().toLowerCase(Locale.ENGLISH)),
						//# else
						//- new ResourceLocation(MOD_ID, "toggle_" + playerModelPart.getId().toLowerCase(Locale.ENGLISH)),
						//# end
						InputConstants.Type.KEYSYM,
						-1,
						SKIN_LAYER_CATEGORY,
						playerModelPart
				))
                .forEach(KeyBindingHelper::registerKeyBinding);
    }

    public static void sendToggleMessage(Player player, boolean value, Component option) {
		//# if MC_VERSION_NUMBER >= 11900
		player.displayClientMessage(Component.translatable("amecs.toggled." + (value ? "on" : "off"), option), true);
		//# else
		//- player.displayClientMessage(new TranslatableComponent("amecs.toggled." + (value ? "on" : "off"), option), true);
		//# end
    }

    public static boolean entryKeyMatches(
			//# if MC_VERSION_NUMBER >= 11802
			KeyBindsList.KeyEntry entry,
			//# else
			//- ControlList.KeyEntry entry,
			//# end
			String keyFilter
	) {
        if (keyFilter == null) {
            return true;
        }
        switch (keyFilter) {
            case "":
                return ((IKeyBindingEntry) entry).amecs$getKeyBinding().isUnbound();
            case "%":
				//# if MC_VERSION_NUMBER >= 12109
				return ((ControlsListWidgetKeyBindingEntryAccessor) entry).getHasCollision();
				//# elif MC_VERSION_NUMBER >= 11600
				//- return ((ControlsListWidgetKeyBindingEntryAccessor) entry).getChangeButton()
				//- 		.getMessage().getStyle().getColor() == TextColor.fromLegacyFormat(ChatFormatting.RED);
				//# else
				//- return ((ControlsListWidgetKeyBindingEntryAccessor) entry).getChangeButton()
				//- 		.getMessage().startsWith(ChatFormatting.RED.toString());
				//# end
            default:
                return StringUtils.containsIgnoreCase(
						//# if MC_VERSION_NUMBER >= 11600
						((IKeyBindingEntry) entry).amecs$getKeyBinding().getTranslatedKeyMessage().getString(),
						//# else
						//- ((IKeyBindingEntry) entry).amecs$getKeyBinding().getTranslatedKeyMessage(),
						//# end
						keyFilter
				);
        }
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "{}: {}", LOGGER_PREFIX, message);
    }

	public static void log(Level level, String message, Throwable throwable) {
		LOGGER.log(level, "{}: {}", LOGGER_PREFIX, message, throwable);
	}
}
