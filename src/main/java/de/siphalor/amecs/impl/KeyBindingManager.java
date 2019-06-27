package de.siphalor.amecs.impl;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.api.ListeningKeyBinding;
import de.siphalor.amecs.util.IKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyBindingManager {
	public static Map<InputUtil.KeyCode, ConcurrentLinkedQueue<KeyBinding>> keysById = new HashMap<>();

	public static void register(KeyBinding keyBinding) {
		InputUtil.KeyCode keyCode = ((IKeyBinding) keyBinding).amecs$getKeyCode();
        if(keysById.containsKey(keyCode)) {
        	keysById.get(keyCode).add(keyBinding);
		} else {
        	keysById.put(keyCode, new ConcurrentLinkedQueue<>(Collections.singleton(keyBinding)));
		}
	}

	public static Stream<KeyBinding> getMatchingKeyBindings(InputUtil.KeyCode keyCode) {
		Stream<KeyBinding> result = keysById.get(keyCode).stream().filter(keyBinding -> ((IKeyBinding) keyBinding).amecs$getKeyModifiers().match());
		Set<KeyBinding> keyBindings = result.collect(Collectors.toSet());
		if(keyBindings.isEmpty())
			return keysById.get(keyCode).stream().filter(keyBinding -> ((IKeyBinding) keyBinding).amecs$getKeyModifiers().isUnset());
		return keyBindings.stream();
	}

	public static void onKeyPressed(InputUtil.KeyCode keyCode) {
		Collection<KeyBinding> keyBindings = KeyBindingManager.keysById.get(keyCode);
		if(keyBindings == null) return;
		KeyBindingManager.getMatchingKeyBindings(keyCode).forEach(keyBinding -> {
			((IKeyBinding) keyBinding).amecs$setTimesPressed(((IKeyBinding) keyBinding).amecs$getTimesPressed() + 1);
			if(keyBinding instanceof ListeningKeyBinding) ((ListeningKeyBinding) keyBinding).onPressed();
		});
	}

	public static void setKeyPressed(InputUtil.KeyCode keyCode, boolean pressed) {
		Amecs.CURRENT_MODIFIERS.set(KeyModifier.fromKeyCode(keyCode.getKeyCode()), pressed);

		Collection<KeyBinding> keyBindings = KeyBindingManager.keysById.get(keyCode);
		if(keyBindings == null) return;
		KeyBindingManager.getMatchingKeyBindings(keyCode).forEach(keyBinding -> ((IKeyBinding) keyBinding).amecs$setPressed(pressed));
	}

	public static void updatePressedStates() {
		Collection<KeyBinding> keyBindings = KeyBindingManager.keysById.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
		for(KeyBinding keyBinding : keyBindings) {
			boolean pressed = !keyBinding.isNotBound() && ((IKeyBinding) keyBinding).amecs$getKeyCode().getCategory() == InputUtil.Type.KEYSYM && InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), ((IKeyBinding) keyBinding).amecs$getKeyCode().getKeyCode());
			((IKeyBinding) keyBinding).amecs$setPressed(pressed);
		}
	}

	public static void updateKeysByCode() {
		keysById.clear();
		KeyBindingUtils.getIdToKeyBindingMap().values().forEach(KeyBindingManager::register);
	}

	public static void unpressAll() {
		KeyBindingUtils.getIdToKeyBindingMap().values().forEach(keyBinding -> ((IKeyBinding) keyBinding).amecs$setPressed(false));
	}
}
