/*
 * Copyright 2026 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.siphalor.amecs.key_mapping_matchers.impl;

import de.siphalor.amecs.key_mapping_matchers.api.AmecsKeyMappingMatchResult;
import de.siphalor.amecs.key_mapping_matchers.api.AmecsKeyMappingMatchersApi;
import de.siphalor.amecs.key_mapping_matchers.impl.duck.IKeyMapping;
import java.util.*;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

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

		List<KeyMapping> exactMatches = new  ArrayList<>();
		List<KeyMapping> fuzzyMatches = new  ArrayList<>();
		for (KeyMapping mapping : mappingsForInput) {
			AmecsKeyMappingMatchResult matchResult = AmecsKeyMappingMatchersApi.IS_ACTIVE.invoker().matchIsActive(mapping);
			if (matchResult == AmecsKeyMappingMatchResult.EXACT_MATCH) {
				exactMatches.add(mapping);
			} else if (exactMatches.isEmpty() && matchResult == AmecsKeyMappingMatchResult.FUZZY_MATCH) {
				fuzzyMatches.add(mapping);
			}
		}
		return (exactMatches.isEmpty() ? fuzzyMatches : exactMatches).stream();
	}
}
