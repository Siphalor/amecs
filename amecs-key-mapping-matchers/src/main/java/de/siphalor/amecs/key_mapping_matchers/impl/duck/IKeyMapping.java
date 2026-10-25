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

package de.siphalor.amecs.key_mapping_matchers.impl.duck;

import com.mojang.blaze3d.platform.InputConstants;

public interface IKeyMapping {
	InputConstants.Key amecs$getBoundKey();

	void amecs$reset();

	//# if MC_VERSION_NUMBER < 11500
	//- void amecs$setDown(boolean pressed);
	//# end

	void amecs$incrementTimesPressed();
}
