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

package de.siphalor.amecs.key_modifiers.impl;

import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifier;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifierCombination;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiers;
import de.siphalor.amecs.key_modifiers.api.AmecsKeyModifiersApi;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
@CustomLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AmecsKeyModifierOptions {
	private static final String KEY_MODIFIERS_PREFIX = "key_modifiers_";
	private static final File OLD_OPTIONS_FILE = new File(Minecraft.getInstance().gameDirectory, "options.amecsapi.txt");
	private static final File OPTIONS_FILE = new File(Minecraft.getInstance().gameDirectory, "options.amecs.key_modifiers.txt");

	private static final Int2ObjectMap<AmecsKeyModifier> KEY_MODIFIERS_BY_LEGACY_ID = new Int2ObjectArrayMap<>();

	private static void initLegacyModifiers() {
		AmecsKeyModifiers.getAll().forEach(modifier -> {
			//noinspection deprecation
			if (modifier.getLegacyId() != null) {
				//noinspection deprecation
				KEY_MODIFIERS_BY_LEGACY_ID.put((int) modifier.getLegacyId(), modifier);
			}
		});
	}

	public static void write(KeyMapping[] allKeyBindings) {
		initLegacyModifiers();

		List<KeyMapping> bindingsWithChangedModifiers = new ArrayList<>(allKeyBindings.length);
		for (KeyMapping keyBinding : allKeyBindings) {
			if (!keyBinding.isDefault()) {
				bindingsWithChangedModifiers.add(keyBinding);
			}
		}

		if (bindingsWithChangedModifiers.isEmpty()) {
			if (OPTIONS_FILE.exists()) {
				try {
					Files.delete(OPTIONS_FILE.toPath());
				} catch (IOException e) {
					log.error("Failed to cleanup Amecs API key binding modifier file - weird.", e);
				}
			}
			return;
		}

		try (Writer writer = new BufferedWriter(new FileWriter(OPTIONS_FILE, StandardCharsets.UTF_8))) {
			AmecsKeyModifierCombination modifiers;
			for (KeyMapping binding : bindingsWithChangedModifiers) {
				modifiers = AmecsKeyModifiersApi.getBoundModifiers(binding);
				writer.write(KEY_MODIFIERS_PREFIX);
				writer.write(binding.getName());
				writer.write(": ");
				writer.write(serializeKeyModifiers(modifiers));
				writer.write('\n');
			}
			writer.flush();
		} catch (IOException e) {
			log.error("Failed to save Amecs API modifiers to options file", e);
		}
	}

	public static void read() {
		initLegacyModifiers();

		File optionsFile = OLD_OPTIONS_FILE.exists() ? OLD_OPTIONS_FILE : OPTIONS_FILE;

		try (BufferedReader reader = new BufferedReader(new FileReader(optionsFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				readLine(line);
			}

			if (optionsFile == OLD_OPTIONS_FILE) {
				//noinspection ResultOfMethodCallIgnored
				optionsFile.delete();
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
			KeyMapping mapping = AmecsKeyModifiersModule.getIdToKeyBindingMap().get(id);
			if (mapping == null) {
				log.warn("Unknown keybinding identifier in Amecs API options file: {}", id);
				return;
			}

			AmecsKeyModifierCombination modifiers = deserializeKeyModifiers(line.substring(colon + 1));
			if (mapping.isUnbound()) {
				if (!modifiers.isUnset()) {
					log.warn("Found modifiers for unbound keybinding in Amecs API options file. Ignoring them: {}", id);
				}
				return;
			}
			AmecsKeyModifiersApi.getBoundModifiers(mapping).copyFrom(modifiers);
		} catch (Throwable e) {
			log.error("Invalid line in Amecs API options file: {}", line, e);
		}
	}

	/**
	 * FOR INTERNAL USE ONLY
	 *
	 * @return the serialized string representation of the modifiers
	 */
	@ApiStatus.Internal
	public static String serializeKeyModifiers(AmecsKeyModifierCombination keyModifiers) {
		Collection<AmecsKeyModifier> modifiers = keyModifiers.getAll();
		if (modifiers.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (AmecsKeyModifier modifier : modifiers) {
			sb.append(modifier.getName()).append(",");
		}

		// remove trailing comma
		// this will fail if value.length is 0. But that would be useless anyways
		sb.setLength(sb.length() - ",".length());
		return sb.toString();
	}

	@SuppressWarnings("deprecation")
	private static AmecsKeyModifierCombination deserializeKeyModifiers(String value) {
		value = value.trim();

		if (value.isEmpty()) {
			return AmecsKeyModifierCombination.NO_MODIFIERS;
		}

		AmecsKeyModifierCombination keyModifiers = new AmecsKeyModifierCombination();
		// backward compatibility for ultra-old format
		if (!value.contains(",")) {
			try {
				long packedModifiers = Long.parseLong(value, 16);
				for (AmecsKeyModifier keyModifier : AmecsKeyModifiers.getAll()) {
					if (keyModifier.getLegacyId() == null) continue;
					long mask = (1L << keyModifier.getLegacyId());
					keyModifiers.set(keyModifier, (packedModifiers & mask) == mask);
				}
				return keyModifiers;
			} catch (NumberFormatException ignored) {
			}
		}

		int i = 0;
		for (String part : value.split(",")) {
			part = part.trim();
			if ("1".equals(part)) {
				// handle the legacy format
				AmecsKeyModifier modifier = KEY_MODIFIERS_BY_LEGACY_ID.get(i);
				if (modifier != null) {
					keyModifiers.set(modifier, true);
				}
			} else if (!"0".equals(part)) {
				// finally, the new format
				AmecsKeyModifier modifier = AmecsKeyModifiers.getByName(part);
				if (modifier != null) {
					keyModifiers.set(modifier, true);
				} else {
					log.warn("Encountered unknown key modifier {} in config file, modifier will not be applied", part);
				}
			}
			i++;
		}
		return keyModifiers;
	}
}
