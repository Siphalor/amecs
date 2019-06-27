package de.siphalor.amecs.api;

import net.minecraft.client.options.KeyBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Utility methods and constants for Amecs and vanilla key bindings
 */
public class KeyBindingUtils {
	public static final int MOUSE_SCROLL_UP = 512;
	public static final int MOUSE_SCROLL_DOWN = 513;

	private static float lastScrollAmount = 0.0F;
	private static boolean eventPropagationCanceled = false;
	private static Map<String, KeyBinding> idToKeyBindingMap;

	/**
	 * Gets the last (y directional) scroll delta
	 * @return the value
	 */
	public static float getLastScrollAmount() {
		return lastScrollAmount;
	}

	/**
	 * Sets the last (y directional) scroll amount. <b>For internal use only.</b>
	 * @param lastScrollAmount the amount
	 */
	public static void setLastScrollAmount(float lastScrollAmount) {
		KeyBindingUtils.lastScrollAmount = lastScrollAmount;
	}

	public static boolean isEventPropagationCanceled() {
		return eventPropagationCanceled;
	}

	public static void setEventPropagationCanceled(boolean eventPropagationCanceled) {
		KeyBindingUtils.eventPropagationCanceled = eventPropagationCanceled;
	}

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
