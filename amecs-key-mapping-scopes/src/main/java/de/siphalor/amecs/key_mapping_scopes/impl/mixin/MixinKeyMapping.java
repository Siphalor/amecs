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

package de.siphalor.amecs.key_mapping_scopes.impl.mixin;

import de.siphalor.amecs.key_mapping_scopes.api.AmecsKeyMappingScope;
import de.siphalor.amecs.key_mapping_scopes.impl.duck.IKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public class MixinKeyMapping implements IKeyMapping {
	@Unique
	private static final AmecsKeyMappingScope[] ALL = AmecsKeyMappingScope.all().toArray(new AmecsKeyMappingScope[0]);

	@Unique
	private AmecsKeyMappingScope[] scopes;

	@Unique
	private boolean scopesExplicitlySet;

	@Override
	public AmecsKeyMappingScope[] amecs$getScopes() {
		return scopes == null ? ALL : scopes;
	}

	@Override
	public void amecs$setScopes(AmecsKeyMappingScope[] scopes) {
		this.scopes = scopes;
	}

	@Override
	public boolean amecs$areScopesExplicitlySet() {
		return scopesExplicitlySet;
	}

	@Override
	public void amecs$markScopesExplicitlySet() {
		this.scopesExplicitlySet = true;
	}
}
