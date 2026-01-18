package de.siphalor.amecs.key_modifiers.impl;

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiersApi;
import de.siphalor.amecs.key_modifiers.impl.duck.IKeyMapping;
import net.minecraft.client.KeyMapping;

import java.util.*;
import java.util.stream.Stream;

public class AmecsKeyMappingManagerLayer {
	private final Map<InputConstants.Key, Collection<KeyMapping>> mappingsByInput = new HashMap<>(100);

	public void clear() {
		mappingsByInput.clear();
	}

	public boolean register(KeyMapping mapping) {
		if (!accepts(mapping)) return false;

		InputConstants.Key boundInput = ((IKeyMapping) mapping).amecs$getBoundKey();

		Collection<KeyMapping> mappingsForInput = mappingsByInput.computeIfAbsent(boundInput, k -> new ArrayList<>());
		if (mappingsForInput.add(mapping)) {
			return false;
		}
		return mappingsForInput.add(mapping);
	}

	public boolean accepts(KeyMapping mapping) {
		return true;
	}

	public boolean unregister(KeyMapping mapping) {
		InputConstants.Key boundInput = ((IKeyMapping) mapping).amecs$getBoundKey();

		Collection<KeyMapping> mappingsForInput = mappingsByInput.get(boundInput);
		if (mappingsForInput == null) return false;

		return mappingsForInput.removeAll(Collections.singleton(mapping));
	}

	public Stream<KeyMapping> getAllMappings() {
		return mappingsByInput.values().stream().flatMap(Collection::stream);
	}

	public Stream<KeyMapping> getMappingsForInput(InputConstants.Key input) {
		Collection<KeyMapping> mappingsForInput = mappingsByInput.getOrDefault(input, Collections.emptyList());
		if (mappingsForInput.isEmpty()) return Stream.empty();

		// If there are two key bindings, alt + y and shift + y, and you press shift + alt + y, both will be triggered.
		// This is intentional.
		List<KeyMapping> matchingMappings = mappingsForInput.stream()
				.filter(AmecsKeyMappingManagerLayer::areExactModifiersPressed)
				.toList();
		if (matchingMappings.isEmpty())
			return mappingsForInput.stream().filter(mapping -> AmecsKeyModifiersApi.getBoundModifiers(mapping).isUnset());
		return matchingMappings.stream();
	}

	private static boolean areExactModifiersPressed(KeyMapping keyBinding) {
		return AmecsKeyModifiersApi.getBoundModifiers(keyBinding).equals(AmecsKeyModifiersModule.CURRENT_MODIFIERS);
	}
}
