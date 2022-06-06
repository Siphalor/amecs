package de.siphalor.amecs;

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
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
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

	public static final KeyBinding ESCAPE_KEYBINDING = KeyBindingHelper.registerKeyBinding(new AmecsKeyBinding(new Identifier(MOD_ID, "alternative_escape"), InputUtil.Type.KEYSYM, -1, "key.categories.ui", new KeyModifiers()));

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(new ToggleAutoJumpKeyBinding(new Identifier(MOD_ID, "toggle_auto_jump"), InputUtil.Type.KEYSYM, 66, "key.categories.movement", new KeyModifiers()));

        Arrays.stream(PlayerModelPart.values())
                .map(playerModelPart -> new SkinLayerKeyBinding(new Identifier(MOD_ID, "toggle_" + playerModelPart.getName().toLowerCase(Locale.ENGLISH)), InputUtil.Type.KEYSYM, -1, SKIN_LAYER_CATEGORY, playerModelPart))
                .forEach(KeyBindingHelper::registerKeyBinding);
    }

    public static void sendToggleMessage(PlayerEntity playerEntity, boolean value, Text option) {
        playerEntity.sendMessage(new TranslatableText("amecs.toggled." + (value ? "on" : "off"), option), true);
    }

    public static boolean entryKeyMatches(ControlsListWidget.KeyBindingEntry entry, String keyFilter) {
        if (keyFilter == null) {
            return true;
        }
        switch (keyFilter) {
            case "":
                return ((IKeyBindingEntry) entry).amecs$getKeyBinding().isUnbound();
            case "%":
                return ((ControlsListWidgetKeyBindingEntryAccessor) entry).getEditButton().getMessage().getStyle().getColor() == TextColor.fromFormatting(Formatting.RED);
            default:
                return StringUtils.containsIgnoreCase(((IKeyBindingEntry) entry).amecs$getKeyBinding().getBoundKeyLocalizedText().getString(), keyFilter);
        }
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, LOGGER_PREFIX + message);
    }
}
