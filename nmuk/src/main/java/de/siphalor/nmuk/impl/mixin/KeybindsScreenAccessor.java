/*
 * Copyright 2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.siphalor.nmuk.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//- import net.minecraft.client.gui.screens.controls.ControlList;
//- import net.minecraft.client.gui.screens.controls.ControlsScreen;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
//# else
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
//- import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
//# end

//# if MC_VERSION_NUMBER >= 11800
@Mixin(KeyBindsScreen.class)
public interface KeybindsScreenAccessor {
	@Accessor
	KeyBindsList getKeyBindsList();
}
//# else
//- @Mixin(ControlsScreen.class)
//- public interface KeybindsScreenAccessor {
//- 	@Accessor
//- 	ControlList getControlList();
//- }
//# end
