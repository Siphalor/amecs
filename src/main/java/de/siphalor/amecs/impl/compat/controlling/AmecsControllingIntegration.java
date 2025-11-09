/*
 * Copyright 2020-2023 Siphalor
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

package de.siphalor.amecs.impl.compat.controlling;

import com.blamejared.controlling.api.event.ControllingEvents;
import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifier;
import de.siphalor.amecs.impl.KeyBindingEditGuiHelper;

//# if CONTROLLING_INTEGRATION
public class AmecsControllingIntegration {
	public static void initialize() {
		ControllingEvents.IS_KEY_CODE_MODIFIER_EVENT.register(
				event -> KeyModifier.fromKey(event.key()) != null
		);
		ControllingEvents.SET_TO_DEFAULT_EVENT.register(event -> {
			KeyBindingUtils.resetBoundModifiers(event.mapping());
			return false;
		});
		ControllingEvents.SET_KEY_EVENT.register(event -> {
			KeyBindingEditGuiHelper.handleKeyPress(event.mapping(), event.key());
			return true;
		});
	}
}
//# end
