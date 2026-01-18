package de.siphalor.amecs.priority_key_mappings.impl.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
	@Accessor
	static @NotNull Map<InputConstants.Key, List<KeyMapping>> getMAP() {
		//noinspection DataFlowIssue
		return null;
	}
}
