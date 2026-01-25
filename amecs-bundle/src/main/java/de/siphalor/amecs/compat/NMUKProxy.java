/*
 * Copyright 2026 Siphalor
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

package de.siphalor.amecs.compat;

import de.siphalor.nmuk.api.NMUKAlternatives;

import net.minecraft.client.KeyMapping;

public class NMUKProxy {
	public static boolean isAlternative(KeyMapping binding) {
		return NMUKAlternatives.isAlternative(binding);
	}
}
