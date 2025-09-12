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

import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.nmuk.NMUK;
import de.siphalor.nmuk.impl.IKeyBinding;
import de.siphalor.nmuk.impl.NMUKKeyBindingHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Mixin(value = Options.class, priority = 800)
public class MixinGameOptions {
	@Unique
	private File nmukOptionsFile;
	@Unique
	private KeyMapping[] tempKeysAll;

	@Mutable
	@Shadow
	@Final
	public KeyMapping[] keyMappings;

	// Prevent nmuk keybindings from getting saved to the Vanilla options file
	@Inject(
			method = "processOptions",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;keyMappings:[Lnet/minecraft/client/KeyMapping;")
	)
	public void removeNMUKBindings(CallbackInfo ci) {
		tempKeysAll = keyMappings;
		keyMappings = Arrays.stream(keyMappings).filter(binding -> !((IKeyBinding) binding).nmuk_isAlternative()).toArray(KeyMapping[]::new);
	}

	@Inject(
			method = "processOptions",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/sounds/SoundSource;values()[Lnet/minecraft/sounds/SoundSource;")
	)
	public void resetAllKeys(CallbackInfo ci) {
		keyMappings = tempKeysAll;
	}

	@Inject(
			method = "save",
			at = @At("RETURN")
	)
	public void save(CallbackInfo ci) {
		try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(nmukOptionsFile), StandardCharsets.UTF_8))) {
			for (KeyMapping binding : keyMappings) {
				if (((IKeyBinding) binding).nmuk_isAlternative()) {
					printWriter.println("key_" + binding.getName() + ":" + binding.saveString());
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Inject(
			method = "load",
			at = @At("RETURN")
	)
	public void load(CallbackInfo ci) {
		if (nmukOptionsFile == null) {
			nmukOptionsFile = new File(Minecraft.getInstance().gameDirectory, "options." + NMUK.MOD_ID + ".txt");
		}

		if (!nmukOptionsFile.exists()) {
			return;
		}
		Map<String, KeyMapping> keyBindings = KeyMapping.ALL;
		Object2IntMap<KeyMapping> alternativeCountMap = new Object2IntOpenHashMap<>();
		Queue<KeyMapping> newAlternatives = new ArrayDeque<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(nmukOptionsFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					int stringIndex = line.lastIndexOf(':');
					if (stringIndex <= 0) {
						NMUK.log(Level.WARN, "Invalid nmuk options line: " + line);
						continue;
					}
					String id = line.substring(0, stringIndex);
					String keyId = line.substring(stringIndex + 1);
					if (!id.startsWith("key_")) {
						NMUK.log(Level.WARN, "Invalid nmuk options entry: " + id);
						continue;
					}
					id = id.substring(4);
					stringIndex = id.indexOf('%');
					if (stringIndex <= 0) {
						NMUK.log(Level.WARN, "Nmuk entry is missing an alternative id");
						continue;
					}
					short altId = Short.parseShort(id.substring(stringIndex + 1));
					id = id.substring(0, stringIndex);
					InputConstants.Key boundKey = InputConstants.getKey(keyId);
					//noinspection ConstantConditions
					KeyMapping base = keyBindings.get(id);
					if (base != null) {
						int index = alternativeCountMap.getOrDefault(base, 0);
						List<KeyMapping> children = ((IKeyBinding) base).nmuk_getAlternatives();
						((IKeyBinding) base).nmuk_setNextChildId(altId);
						if (children == null || index >= children.size()) {
							KeyMapping alternative = NMUKKeyBindingHelper.createAlternativeKeyBinding(base);
							alternative.setKey(boundKey);
							newAlternatives.add(alternative);
						} else {
							children.get(index).setKey(boundKey);
						}
						alternativeCountMap.put(base, index + 1);
					}
				} catch (Throwable e) {
					NMUK.log(Level.ERROR, "Encountered an issue whilst loading nmuk options file!");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Set<KeyMapping> newAllKeyMappings = new TreeSet<>(Arrays.asList(keyMappings));

		newAllKeyMappings.addAll(newAlternatives);

		int newCount, oldCount;
		for (KeyMapping binding : keyMappings) {
			newCount = alternativeCountMap.getOrDefault(binding, 0);
			oldCount = ((IKeyBinding) binding).nmuk_getAlternativesCount();
			if (oldCount > newCount) {
				List<KeyMapping> alternatives = ((IKeyBinding) binding).nmuk_getAlternatives();
				List<KeyMapping> obsoleteAlternatives = alternatives.subList(newCount, oldCount);

				for (KeyMapping obsoleteAlternative : obsoleteAlternatives) {
					newAllKeyMappings.remove(obsoleteAlternative);
					KeyMapping.ALL.remove(obsoleteAlternative.getName());
				}
				obsoleteAlternatives.clear();
			}
		}
		keyMappings = newAllKeyMappings.toArray(new KeyMapping[0]);
		KeyMapping.resetMapping();
	}
}
