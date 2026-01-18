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

package de.siphalor.nmuk.impl;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.KeyMapping;

@ApiStatus.Internal
public interface IKeyBinding {
	//# if MC_VERSION_NUMBER < 11500
	//- void nmuk_setDown(boolean down);
	//# end

	short nmuk_getNextChildId();

	short nmuk_claimNextChildId();

	void nmuk_setNextChildId(short nextChildId);

	boolean nmuk_isAlternative();

	KeyMapping nmuk_getParent();

	void nmuk_setParent(KeyMapping binding);

	List<KeyMapping> nmuk_getAlternatives();

	int nmuk_getAlternativesCount();

	void nmuk_removeAlternative(KeyMapping binding);

	void nmuk_addAlternative(KeyMapping binding);

	int nmuk_getIndexInParent();
}
