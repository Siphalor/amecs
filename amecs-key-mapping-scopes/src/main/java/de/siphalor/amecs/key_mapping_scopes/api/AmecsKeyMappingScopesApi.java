package de.siphalor.amecs.key_mapping_scopes.api;

import de.siphalor.amecs.key_mapping_scopes.impl.duck.IKeyMapping;
import java.util.Set;

import net.minecraft.client.KeyMapping;

public class AmecsKeyMappingScopesApi {
	public static void setScopes(KeyMapping keyMapping, Set<AmecsKeyMappingScope> scopes) {
		setScopes(keyMapping, scopes.toArray(new AmecsKeyMappingScope[0]));
	}

	public static void setScopes(KeyMapping keyMapping, AmecsKeyMappingScope... scopes) {
		IKeyMapping iKeyMapping = (IKeyMapping) keyMapping;
		iKeyMapping.amecs$setScopes(scopes);
		iKeyMapping.amecs$markScopesExplicitlySet();
	}
}
