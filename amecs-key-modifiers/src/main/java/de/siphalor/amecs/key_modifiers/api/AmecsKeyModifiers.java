package de.siphalor.amecs.key_modifiers.api;

import de.siphalor.amecs.key_modifiers.impl.DefaultKeyModifier;
import java.util.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.InputConstants;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AmecsKeyModifiers {
	private static final Map<String, AmecsKeyModifier> MODIFIERS_BY_NAME = new HashMap<>();
	static final List<AmecsKeyModifier> MODIFIERS = new ArrayList<>(5);
	private static boolean sealed = false;

	public static AmecsKeyModifier ALT = new DefaultKeyModifier("alt", 0, 342, 346);
	public static AmecsKeyModifier SHIFT = new DefaultKeyModifier("shift", 2, 340, 344);
	public static AmecsKeyModifier CONTROL = new DefaultKeyModifier("control", 1, 341, 345);

	public static void register(AmecsKeyModifier keyModifier) {
		requireUnsealed();

		keyModifier.arrayIndex = MODIFIERS.size();
		MODIFIERS_BY_NAME.put(keyModifier.getName(), keyModifier);
		MODIFIERS.add(keyModifier);
	}

	@ApiStatus.Internal
	public static void seal() {
		requireUnsealed();

		sealed = true;
	}

	public static Collection<AmecsKeyModifier> getAll() {
		requireSealed();

		return MODIFIERS;
	}

	public static @Nullable AmecsKeyModifier getByName(String name) {
		return MODIFIERS_BY_NAME.get(name);
	}

	public static @Nullable AmecsKeyModifier fromKeyCode(int keyCode) {
		requireSealed();

		for (AmecsKeyModifier keyModifier : MODIFIERS_BY_NAME.values()) {
			if (keyModifier.matches(keyCode)) {
				return keyModifier;
			}
		}
		return null;
	}

	public static @Nullable AmecsKeyModifier fromKey(InputConstants.Key key) {
		requireSealed();

		if (key == null || key.getType() != InputConstants.Type.KEYSYM) {
			return null;
		}
		return fromKeyCode(key.getValue());
	}

	private static void requireUnsealed() {
		if (sealed) {
			throw new IllegalStateException("Cannot register new key modifiers after the API has been sealed");
		}
	}

	private static void requireSealed() {
		if (!sealed) {
			throw new IllegalStateException("Cannot access key modifiers before the API has been sealed");
		}
	}
}
