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

import de.siphalor.nmuk.NMUK;
import de.siphalor.nmuk.impl.IKeyBinding;
import de.siphalor.nmuk.impl.NMUKKeyBindingHelper;
//- import de.siphalor.nmuk.impl.util.IdentityHashSet;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

@Mixin(Options.class)
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
			//# if MC_VERSION_NUMBER >= 11700
			method = "processOptions",
			//# else
			//- method = "save",
			//# end
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;keyMappings:[Lnet/minecraft/client/KeyMapping;")
	)
	public void removeNMUKBindings(CallbackInfo ci) {
		tempKeysAll = keyMappings;
		keyMappings = Arrays.stream(keyMappings).filter(binding -> !((IKeyBinding) binding).nmuk_isAlternative()).toArray(KeyMapping[]::new);
	}

	@Inject(
			//# if MC_VERSION_NUMBER >= 11700
			method = "processOptions",
			//# else
			//- method = "save",
			//# end
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
			NMUK.log(Level.ERROR, "Failed to save NMUK key binding alternatives", e);
		}
	}

	@Inject(
			method = "load",
			at = @At("RETURN"),
			// Load before Amecs Key Modifiers, so we can create the key bindings accordingly
			order = 800
	)
	public void load(CallbackInfo ci) {
		if (nmukOptionsFile == null) {
			nmukOptionsFile = new File(Minecraft.getInstance().gameDirectory, "options." + NMUK.MOD_ID + ".txt");
		}

		if (!nmukOptionsFile.exists()) {
			return;
		}
		Map<String, KeyMapping> keyBindings = KeyMapping.ALL;
		Set<String> encounteredKeyBindingNames = new HashSet<>(keyMappings.length * 2);
		List<KeyMapping> newAlternatives = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(nmukOptionsFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					int stringIndex = line.lastIndexOf(':');
					if (stringIndex <= 0) {
						NMUK.log(Level.WARN, "Invalid nmuk options line: " + line);
						continue;
					}
					String name = line.substring(0, stringIndex);
					String keyId = line.substring(stringIndex + 1);
					if (!name.startsWith("key_")) {
						NMUK.log(Level.WARN, "Invalid nmuk options entry: " + name);
						continue;
					}
					name = name.substring(4);
					stringIndex = name.indexOf('%');
					if (stringIndex <= 0) {
						NMUK.log(Level.WARN, "Nmuk entry is missing an alternative id");
						continue;
					}
					short altId = Short.parseShort(name.substring(stringIndex + 1));

					String baseName = name.substring(0, stringIndex);
					InputConstants.Key boundKey = InputConstants.getKey(keyId);

					KeyMapping baseBinding = keyBindings.get(baseName);
					if (baseBinding == null) {
						NMUK.log(Level.WARN, "Key binding " + baseName + " doesn't exist");
						continue;
					}

					IKeyBinding baseBindingAccessor = (IKeyBinding) baseBinding;
					baseBindingAccessor.nmuk_setNextChildId(
							(short) Math.max(altId, baseBindingAccessor.nmuk_getNextChildId())
					);

					KeyMapping altBinding = keyBindings.get(name);
					if (altBinding == null) {
						altBinding = NMUKKeyBindingHelper.createAlternativeKeyBindingWithName(
								baseBinding,
								name,
								boundKey
						);
						newAlternatives.add(altBinding);
					}

					altBinding.setKey(boundKey);
					encounteredKeyBindingNames.add(name);
				} catch (Throwable e) {
					NMUK.log(Level.WARN, "Encountered an issue whilst loading nmuk options file!", e);
				}
			}
		} catch (IOException e) {
			NMUK.log(Level.ERROR, "Failed to load nmuk options file");
		}

		//# if MC_VERSION_NUMBER >= 11600
		Set<KeyMapping> newAllKeyMappings = new TreeSet<>(Arrays.asList(keyMappings));
		//# else
		//- Set<KeyMapping> newAllKeyMappings = new IdentityHashSet<>(keyMappings.length + newAlternatives.size());
		//- newAllKeyMappings.addAll(Arrays.asList(keyMappings));
		//# end

		newAllKeyMappings.addAll(newAlternatives);

		for (KeyMapping keyMapping : keyMappings) {
			if (!encounteredKeyBindingNames.contains(keyMapping.getName())) {
				KeyMapping parent = ((IKeyBinding) keyMapping).nmuk_getParent();
				if (parent == null) {
					continue;
				}

				List<KeyMapping> alternatives = ((IKeyBinding) parent).nmuk_getAlternatives();
				alternatives.remove(keyMapping);
				newAllKeyMappings.remove(keyMapping);
				KeyMapping.ALL.remove(keyMapping.getName());
			}
		}

		//# if MC_VERSION_NUMBER >= 11600
		keyMappings = newAllKeyMappings.toArray(new KeyMapping[0]);
		//# else
		//- List<KeyMapping> sortedKeyMappings = new ArrayList<>(newAllKeyMappings);
		//- sortedKeyMappings.sort(
		//- 		Comparator.<KeyMapping, Integer>comparing(km -> KeyBindingAccessor.getCATEGORY_SORT_ORDER().getOrDefault(km.getCategory(), 99))
		//- 				.thenComparing(KeyMapping::getCategory)
		//- 				.thenComparing(KeyMapping::getName)
		//- );
		//- keyMappings = sortedKeyMappings.toArray(new KeyMapping[0]);
		//# end

		KeyMapping.resetMapping();
	}
}
