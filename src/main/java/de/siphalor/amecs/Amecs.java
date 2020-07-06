package de.siphalor.amecs;

import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.keybindings.SkinLayerKeyBinding;
import de.siphalor.amecs.keybindings.ToggleAutoJumpKeyBinding;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Arrays;

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

    private static final String SKIN_LAYER_CATEGORY = MOD_ID + ".key.categories.skin_layers";

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(new ToggleAutoJumpKeyBinding(new Identifier(MOD_ID, "toggle_auto_jump"), InputUtil.Type.KEYSYM, 66,
                                             "key.categories.movement", new KeyModifiers()));

        KeyBinding[] skinLayerKeyBindings = Arrays.stream(PlayerModelPart.values()).map(playerModelPart -> new SkinLayerKeyBinding(new Identifier(MOD_ID, "toggle_" + playerModelPart.getName()), InputUtil.Type.KEYSYM, -1, SKIN_LAYER_CATEGORY, playerModelPart)).toArray(KeyBinding[]::new);
        Arrays.stream(skinLayerKeyBindings).forEach(KeyBindingHelper::registerKeyBinding);
    }

    public static void sendToggleMessage(PlayerEntity playerEntity, boolean value, Text option) {
        playerEntity.sendMessage(new TranslatableText("amecs.toggled." + (value ? "on" : "off"), option), true);
    }
}
