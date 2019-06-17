package de.siphalor.amecs;

import net.minecraft.client.options.KeyBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class Amecs {
    public static final String MOD_ID = "amecs";
    public static final String KEY_MODIFIER_GAME_OPTION = MOD_ID + "$key_modifier$";

    static Map<String, KeyBinding> idToKeyBindingMap;

    public static boolean isShiftKey(int keyCode) {
        return keyCode == 340 || keyCode == 344;
    }

    public static boolean isControlKey(int keyCode) {
        return keyCode == 341 || keyCode == 345;
    }

    public static boolean isAltKey(int keyCode) {
        return keyCode == 342 || keyCode == 346;
    }

    public static boolean isModifier(int keyCode) {
        return isShiftKey(keyCode) || isControlKey(keyCode) || isAltKey(keyCode);
    }

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
}
