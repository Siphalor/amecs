package de.siphalor.amecs;

import de.siphalor.amecs.api.KeyModifiers;
import net.minecraft.client.options.KeyBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Main class of Amecs (Alt-Meta-Escape-Control-Shift)
 */
public class Amecs {
    /**
     * The mod id of Amecs
     */
    public static final String MOD_ID = "amecs";
    /**
     * The prefix used in the <code>options.txt</code>
     */
    public static final String KEY_MODIFIER_GAME_OPTION = MOD_ID + "$key_modifier$";

    /**
     * Defines the current pressed key modifiers
     */
    public static final KeyModifiers CURRENT_MODIFIERS = new KeyModifiers();

    private static Map<String, KeyBinding> idToKeyBindingMap;

    /**
     * Gets the "official" idToKeys map
     * @return the map (use with care)
     */
    public static Map<String, KeyBinding> getIdToKeyBindingMap() {
        if(idToKeyBindingMap == null) {
            try {
                //noinspection JavaReflectionMemberAccess
                Method method = KeyBinding.class.getDeclaredMethod("amecs$getIdToKeyBindingMap");
                if(!method.isAccessible()) method.setAccessible(true);
                //noinspection unchecked
                idToKeyBindingMap = (Map<String, KeyBinding>) method.invoke(null);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return idToKeyBindingMap;
    }

    /**
     * Unregisters a keybinding with the given id
     */
    @SuppressWarnings("unused")
    public static void unregisterKeyBinding(String id) {
        getIdToKeyBindingMap().remove(id);
        KeyBinding.updateKeysByCode();
    }
}
