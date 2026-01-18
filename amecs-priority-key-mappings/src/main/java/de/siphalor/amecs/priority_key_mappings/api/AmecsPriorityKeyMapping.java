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

package de.siphalor.amecs.priority_key_mappings.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * An interface to be used on {@link net.minecraft.client.KeyMapping}s.
 * <b>This key binding triggers without further conditions before any other checks or conditions.</b>
 */
@Environment(EnvType.CLIENT)
public interface AmecsPriorityKeyMapping {
	/**
	 * This method gets triggered when this key binding matches on an input event. <br>
	 * Since there are no other checks before the invocation you need to check yourself for possible open screens.
	 *
	 * @return Return true to cancel propagation of this event. Return false for normal evaluation.
	 */
	boolean onPressedPriority();

	/**
	 * This method gets triggered when this key binding matches on an input release event. <br>
	 * Since there are no other checks before the invocation you need to check yourself for possible open screens.
	 *
	 * @return Return true to cancel propagation of this event. Return false for normal evaluation.
	 */
	default boolean onReleasedPriority() {
		return false;
	}
}
