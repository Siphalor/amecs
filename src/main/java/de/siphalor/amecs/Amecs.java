package de.siphalor.amecs;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.duck.IKeyBindingEntry;
import de.siphalor.amecs.keybinding.SkinLayerKeyBinding;
import de.siphalor.amecs.keybinding.ToggleAutoJumpKeyBinding;
import de.siphalor.amecs.mixin.ControlsListWidgetKeyBindingEntryAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.controls.ControlList;
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Locale;

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

    private static final String SKIN_LAYER_CATEGORY = MOD_ID + ".key.categories.skin_layers";

	public static final KeyMapping ESCAPE_KEYBINDING = KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(
			new ResourceLocation(MOD_ID, "alternative_escape"),
			InputConstants.Type.KEYSYM,
			-1,
			"key.categories.ui",
			new KeyModifiers()
	));

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(new ToggleAutoJumpKeyBinding(
				new ResourceLocation(MOD_ID, "toggle_auto_jump"),
				InputConstants.Type.KEYSYM,
				66,
				"key.categories.movement",
				new KeyModifiers()
		));

        Arrays.stream(PlayerModelPart.values())
                .map(playerModelPart -> new SkinLayerKeyBinding(
						new ResourceLocation(MOD_ID, "toggle_" + playerModelPart.getId().toLowerCase(Locale.ENGLISH)),
						InputConstants.Type.KEYSYM,
						-1,
						SKIN_LAYER_CATEGORY,
						playerModelPart
				))
                .forEach(KeyBindingHelper::registerKeyBinding);
    }

    public static void sendToggleMessage(Player player, boolean value, Component option) {
		//# if MC_VERSION_NUMBER >= 11900
		//- player.displayClientMessage(Component.translatable("amecs.toggled." + (value ? "on" : "off"), option), true);
		//# else
		player.displayClientMessage(new TranslatableComponent("amecs.toggled." + (value ? "on" : "off"), option), true);
		//# end
    }

    public static boolean entryKeyMatches(
			//# if MC_VERSION_NUMBER >= 11802
			//- KeyBindsList.KeyEntry entry,
			//# else
			ControlList.KeyEntry entry,
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
                return ((ControlsListWidgetKeyBindingEntryAccessor) entry).getChangeButton()
						.getMessage().getStyle().getColor() == TextColor.fromLegacyFormat(ChatFormatting.RED);
            default:
                return StringUtils.containsIgnoreCase(
						((IKeyBindingEntry) entry).amecs$getKeyBinding().getTranslatedKeyMessage().getString(),
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
