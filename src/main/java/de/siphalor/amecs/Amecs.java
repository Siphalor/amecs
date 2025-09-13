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

	//# if MC_VERSION_NUMBER >= 12109
	private static final KeyMapping.Category SKIN_LAYER_CATEGORY = KeyMapping.Category.register(
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "skin_layers")
	);
	//# else
	//- private static final String SKIN_LAYER_CATEGORY = MOD_ID + ".key.categories.skin_layers";
	//# end

	public static final KeyMapping ESCAPE_KEYBINDING = KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(
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
			new KeyModifiers()
	));

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(new ToggleAutoJumpKeyBinding(
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
				new KeyModifiers()
		));

        Arrays.stream(PlayerModelPart.values())
                .map(playerModelPart -> new SkinLayerKeyBinding(
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
				//# else
				//- return ((ControlsListWidgetKeyBindingEntryAccessor) entry).getChangeButton()
				//- 		.getMessage().getStyle().getColor() == TextColor.fromLegacyFormat(ChatFormatting.RED);
				//# end
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
