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

package de.siphalor.amecs.api.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

/**
 * This interface is used for input event handling and is (un-)registered in {@link InputHandlerManager}
 *
 * @see #handleInput
 * @see InputHandlerManager
 * @deprecated TODO
 */
@Environment(EnvType.CLIENT)
@Deprecated(forRemoval = true)
public interface InputEventHandler {

	/**
	 * This method is called from {@link InputHandlerManager#handleInputEvents(Minecraft)}
	 *
	 * @see InputHandlerManager#handleInputEvents(Minecraft)
	 */
	void handleInput(Minecraft client);

}
