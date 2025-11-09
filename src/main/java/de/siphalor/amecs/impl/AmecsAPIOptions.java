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

package de.siphalor.amecs.impl;

import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.impl.duck.IKeyBinding;
import lombok.CustomLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
@CustomLog
public class AmecsAPIOptions {
	private static final String KEY_MODIFIERS_PREFIX = "key_modifiers_";
	private static final File optionsFile = new File(Minecraft.getInstance().gameDirectory, "options." + AmecsAPI.MOD_ID + ".txt");

	private AmecsAPIOptions() {}

	public static void write(KeyMapping[] allKeyBindings) {
		List<KeyMapping> bindingsWithChangedModifiers = new ArrayList<>(allKeyBindings.length);
		for (KeyMapping keyBinding : allKeyBindings) {
			if (!KeyBindingUtils.getDefaultModifiers(keyBinding).equals(KeyBindingUtils.getBoundModifiers(keyBinding))) {
				bindingsWithChangedModifiers.add(keyBinding);
			}
		}

		if (bindingsWithChangedModifiers.isEmpty()) {
			if (optionsFile.exists()) {
				try {
					Files.delete(optionsFile.toPath());
				} catch (IOException e) {
					log.error("Failed to cleanup Amecs API key binding modifier file - weird.", e);
				}
			}
			return;
		}

		try (PrintWriter writer = new PrintWriter(new FileOutputStream(optionsFile))) {
			KeyModifiers modifiers;
			for (KeyMapping binding : bindingsWithChangedModifiers) {
				modifiers = KeyBindingUtils.getBoundModifiers(binding);
				writer.println(KEY_MODIFIERS_PREFIX + binding.getName() + ":" + modifiers.serializeValue());
			}
		} catch (FileNotFoundException e) {
			log.error("Failed to save Amecs API modifiers to options file", e);
		}
	}

	public static void read() {
		if (!optionsFile.exists()) {
			return;
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(optionsFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				readLine(line);
			}
		} catch (IOException e) {
			log.error("Failed to load Amecs API options file", e);
		}
	}

	private static void readLine(String line) {
		try {
			int colon = line.indexOf(':');
			if (colon <= 0) {
				log.warn("Invalid line in Amecs API options file: {}", line);
				return;
			}
			String id = line.substring(0, colon);
			if (!id.startsWith(KEY_MODIFIERS_PREFIX)) {
				log.warn("Invalid entry in Amecs API options file: {}", id);
				return;
			}
			id = id.substring(KEY_MODIFIERS_PREFIX.length());
			KeyMapping keyBinding = KeyBindingUtils.getIdToKeyBindingMap().get(id);
			if (keyBinding == null) {
				log.warn("Unknown keybinding identifier in Amecs API options file: {}", id);
				return;
			}

			KeyModifiers modifiers = new KeyModifiers(KeyModifiers.deserializeValue(line.substring(colon + 1)));
			if (keyBinding.isUnbound()) {
				if (!modifiers.isUnset()) {
					log.warn("Found modifiers for unbound keybinding in Amecs API options file. Ignoring them: {}", id);
				}
				return;
			}
			((IKeyBinding) keyBinding).amecs$getKeyModifiers().copyModifiers(modifiers);
		} catch (Throwable e) {
			log.error("Invalid line in Amecs API options file: {}", line, e);
		}
	}
}
