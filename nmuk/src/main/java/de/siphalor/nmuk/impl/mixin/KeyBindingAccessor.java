/*
 * Copyright 2021 Siphalor
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

package de.siphalor.nmuk.impl.mixin;

//- import java.util.Collections;
//- import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public interface KeyBindingAccessor {
	//# if MC_VERSION_NUMBER < 11600
	//- @Accessor
	//- static Map<String, Integer> getCATEGORY_SORT_ORDER() {
	//- 	return Collections.emptyMap();
	//- }
	//# end

	@Final
	@Mutable
	@Accessor
	void setName(String id);

	@Final
	@Mutable
	@Accessor
	//# if MC_VERSION_NUMBER >= 12109
	void setCategory(KeyMapping.Category category);
	//# else
	//- void setCategory(String category);
	//# end

	@Accessor
	int getClickCount();

	@Accessor
	void setClickCount(int timesPressed);
}
