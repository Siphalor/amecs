package de.siphalor.amecs;

import de.siphalor.amecs.api.KeyModifiers;

/**
 * Main class of Amecs (Alt-Meta-Escape-Control-Shift)
 */
public class Amecs {
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

}
