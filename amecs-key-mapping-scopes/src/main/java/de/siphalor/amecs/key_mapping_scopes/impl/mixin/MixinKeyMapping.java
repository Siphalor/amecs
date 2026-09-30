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
