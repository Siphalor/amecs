package de.siphalor.amecs;

import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.keybindings.SkinLayerKeyBinding;
import de.siphalor.amecs.impl.keybindings.ToggleAutoJumpKeyBinding;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
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
public class Amecs implements ModInitializer {
    /**
     * The mod id of Amecs
     */
    @SuppressWarnings("WeakerAccess")
    public static final String MOD_ID = "amecs";
    /**
     * The prefix used in the <code>options.txt</code>
     */
    public static final String KEY_MODIFIER_GAME_OPTION = MOD_ID + "$key_modifier$";

    /**
     * Defines the current pressed key modifiers
     */
    public static final KeyModifiers CURRENT_MODIFIERS = new KeyModifiers();

    private static final String SKIN_LAYER_CATEGORY = MOD_ID + ".key.categories.skin_layers";

    @Override
    public void onInitialize() {
        KeyBindingRegistry.INSTANCE.addCategory(SKIN_LAYER_CATEGORY);

        KeyBindingRegistry.INSTANCE.register(new ToggleAutoJumpKeyBinding(new Identifier(MOD_ID, "toggle_auto_jump"), InputUtil.Type.KEYSYM, 66, "key.categories.movement", new KeyModifiers()));

        FabricKeyBinding[] skinLayerKeyBindings = Arrays.stream(PlayerModelPart.values()).map(playerModelPart -> new SkinLayerKeyBinding(new Identifier(MOD_ID, "toggle_" + playerModelPart.getName()), InputUtil.Type.KEYSYM, -1, SKIN_LAYER_CATEGORY, playerModelPart)).toArray(FabricKeyBinding[]::new);
        Arrays.stream(skinLayerKeyBindings).forEach(KeyBindingRegistry.INSTANCE::register);
    }

    public static void sendToggleMessage(PlayerEntity playerEntity, boolean value, Text option) {
        playerEntity.addChatMessage(new TranslatableText("amecs.toggled." + (value ? "on" : "off"), option), true);
    }
}
