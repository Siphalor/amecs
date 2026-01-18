/*
 * Copyright 2020 Siphalor
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

package de.siphalor.amecs.testmod;

import com.mojang.blaze3d.platform.InputConstants;
//- import de.siphalor.amecs.api.AmecsKeyBinding;
//- import de.siphalor.amecs.api.KeyModifiers;
//- import de.siphalor.amecs.api.PriorityKeyBinding;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyBindingWithKeyModifiers;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
import de.siphalor.amecs.priority_key_mappings.api.AmecsPriorityKeyMapping;
import java.util.function.BooleanSupplier;

import net.minecraft.resources.ResourceLocation;

public class TestPriorityKeybinding extends AmecsKeyBindingWithKeyModifiers implements AmecsPriorityKeyMapping {
	private final BooleanSupplier action;
	private final BooleanSupplier releaseAction;

	public TestPriorityKeybinding(
			ResourceLocation id,
			InputConstants.Type type,
			int code,
			//# if MC_VERSION_NUMBER >= 12109
			Category category,
			//# else
			//- String category,
			//# end
			AmecsKeyModifierCombination defaultModifiers,
			BooleanSupplier action,
			BooleanSupplier releaseAction
	) {
		super(id, type, code, category, defaultModifiers);
		this.action = action;
		this.releaseAction = releaseAction;
	}

	@Override
	public boolean onPressedPriority() {
		return action.getAsBoolean();
	}

	@Override
	public boolean onReleasedPriority() {
		return releaseAction.getAsBoolean();
	}
}
