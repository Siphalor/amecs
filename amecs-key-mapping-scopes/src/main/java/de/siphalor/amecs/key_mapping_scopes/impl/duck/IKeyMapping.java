package de.siphalor.amecs.key_mapping_scopes.impl.duck;

import de.siphalor.amecs.key_mapping_scopes.api.AmecsKeyMappingScope;

public interface IKeyMapping {
	AmecsKeyMappingScope[] amecs$getScopes();
	void amecs$setScopes(AmecsKeyMappingScope[] scopes);
	boolean amecs$areScopesExplicitlySet();
	void amecs$markScopesExplicitlySet();
}
