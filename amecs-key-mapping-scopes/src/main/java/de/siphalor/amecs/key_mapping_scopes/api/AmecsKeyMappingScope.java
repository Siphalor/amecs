/*
 * Copyright 2019 Siphalor
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

package de.siphalor.amecs.key_mapping_scopes.api;

import de.siphalor.amecs.key_mapping_scopes.impl.AmecsKeyMappingScopesModule;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import net.minecraft.resources.Identifier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AmecsKeyMappingScope {
	private final Identifier id;

	public static final AmecsKeyMappingScope IN_GAME = new AmecsKeyMappingScope(createId("in_game"));
	public static final AmecsKeyMappingScope INVENTORY = new AmecsKeyMappingScope(createId("inventory"));
	public static final AmecsKeyMappingScope MENU = new AmecsKeyMappingScope(createId("menu"));
	private static final AmecsKeyMappingScope[] ALL = { IN_GAME, INVENTORY, MENU };

	public static Collection<AmecsKeyMappingScope> all() {
		return Collections.unmodifiableList(Arrays.asList(ALL));
	}

	//# if MC_VERSION_NUMBER >= 260100
	private static Identifier createId(String path) {
		return Identifier.fromNamespaceAndPath(AmecsKeyMappingScopesModule.MOD_ID, path);
	}
	//# else
	//- private static ResourceLocation createId(String path) {
	//- 	//# if MC_VERSION_NUMBER >= 12100
	//- 	return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	//- 	//# else
	//- 	return new ResourceLocation(MOD_ID, path);
	//- 	//# end
	//- }
	//# end
}
