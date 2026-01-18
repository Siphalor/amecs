package de.siphalor.amecs.priority_key_mappings.impl.mixin;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
	@Accessor
	static @NotNull Map<InputConstants.Key, List<KeyMapping>> getMAP() {
		//noinspection DataFlowIssue
		return null;
	}
}
