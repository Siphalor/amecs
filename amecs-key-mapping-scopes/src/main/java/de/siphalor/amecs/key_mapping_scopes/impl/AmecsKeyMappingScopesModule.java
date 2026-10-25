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

package de.siphalor.amecs.key_mapping_scopes.impl;

import de.siphalor.amecs.key_mapping_matchers.api.AmecsKeyMappingMatchResult;
import de.siphalor.amecs.key_mapping_matchers.api.AmecsKeyMappingMatchersApi;
import de.siphalor.amecs.key_mapping_scopes.api.AmecsKeyMappingScope;
import de.siphalor.amecs.key_mapping_scopes.impl.duck.IKeyMapping;
import net.fabricmc.api.ClientModInitializer;

public class AmecsKeyMappingScopesModule implements ClientModInitializer {
	public static final String MOD_ID = "amecs_key_mapping_scopes";

	public static AmecsKeyMappingScope currentScope = null;

	@Override
	public void onInitializeClient() {
		AmecsKeyMappingMatchersApi.IS_ACTIVE.register(mapping -> {
			for (AmecsKeyMappingScope scope : ((IKeyMapping) mapping).amecs$getScopes()) {
				if (scope == currentScope) {
					return AmecsKeyMappingMatchResult.EXACT_MATCH;
				}
			}
			return AmecsKeyMappingMatchResult.NO_MATCH;
		});
		AmecsKeyMappingMatchersApi.ON_KEY_MAPPING_REGISTERED.register(mapping -> {
			IKeyMapping iKeyMapping = (IKeyMapping) mapping;
			if (mapping.getName().startsWith("jei_") && !iKeyMapping.amecs$areScopesExplicitlySet()) {
				iKeyMapping.amecs$setScopes(new AmecsKeyMappingScope[]{AmecsKeyMappingScope.INVENTORY});
			}
		});
	}
}
