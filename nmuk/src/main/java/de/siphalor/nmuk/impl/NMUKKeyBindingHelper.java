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

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.blaze3d.platform.InputConstants;
import de.siphalor.nmuk.NMUK;
import de.siphalor.nmuk.impl.mixin.EntryListWidgetAccessor;
import de.siphalor.nmuk.impl.mixin.GameOptionsAccessor;
import de.siphalor.nmuk.impl.mixin.KeybindsScreenAccessor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
//- import net.minecraft.client.gui.screens.controls.ControlList;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
//# else
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
//# end
import net.minecraft.network.chat.Component;

@ApiStatus.Internal
public class NMUKKeyBindingHelper {
	public static final Multimap<KeyMapping, KeyMapping> defaultAlternatives = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
	private static final boolean isAmecsLoaded = FabricLoader.getInstance().isModLoaded("amecsapi");

	public static void removeKeyBinding(KeyMapping binding) {
		GameOptionsAccessor options = (GameOptionsAccessor) Minecraft.getInstance().options;
		KeyMapping[] keysAll = options.getKeyMappings();
		int index = ArrayUtils.indexOf(keysAll, binding);
		if (index < 0) {
			return;
		}
		KeyMapping[] newKeysAll = new KeyMapping[keysAll.length - 1];
		System.arraycopy(keysAll, 0, newKeysAll, 0, index);
		System.arraycopy(keysAll, index + 1, newKeysAll, index, keysAll.length - index - 1);
		options.setKeyMappings(newKeysAll);
		KeyMapping.ALL.remove(binding.getName());
		KeyMapping.resetMapping();
	}

	public static void registerKeyBinding(KeyMapping binding) {
		GameOptionsAccessor options = (GameOptionsAccessor) Minecraft.getInstance().options;
		if (options != null) {
			KeyMapping[] keysAll = options.getKeyMappings();
			KeyMapping[] newKeysAll = new KeyMapping[keysAll.length + 1];
			System.arraycopy(keysAll, 0, newKeysAll, 0, keysAll.length);
			newKeysAll[keysAll.length] = binding;
			options.setKeyMappings(newKeysAll);
		} else {
			KeyBindingHelper.registerKeyBinding(binding);
		}
		KeyMapping.ALL.put(binding.getName(), binding);
		KeyMapping.resetMapping();
	}

	public static void registerKeyBindings(Options gameOptions, Collection<KeyMapping> bindings) {
		GameOptionsAccessor options = (GameOptionsAccessor) gameOptions;
		KeyMapping[] keysAll = options.getKeyMappings();
		KeyMapping[] newKeysAll = new KeyMapping[keysAll.length + bindings.size()];
		System.arraycopy(keysAll, 0, newKeysAll, 0, keysAll.length);
		int i = keysAll.length;
		for (KeyMapping binding : bindings) {
			newKeysAll[i] = binding;
			KeyMapping.ALL.put(binding.getName(), binding);
			i++;
		}
		options.setKeyMappings(newKeysAll);
		KeyMapping.resetMapping();
	}

	public static void resetSingleKeyBinding(KeyMapping keyBinding) {
		keyBinding.setKey(keyBinding.getDefaultKey());
		if (isAmecsLoaded) {
			AmecsProxy.resetKeyModifiers(keyBinding);
		}
	}

	public static KeyMapping createAlternativeKeyBinding(KeyMapping base) {
		return createAlternativeKeyBinding(base, InputConstants.UNKNOWN.getType(), InputConstants.UNKNOWN.getValue());
	}

	public static KeyMapping createAlternativeKeyBinding(KeyMapping base, InputConstants.Type type, int code) {
		IKeyBinding parent = (IKeyBinding) base;
		return createAlternativeKeyBindingWithName(
				base,
				base.getName() + "%" + parent.nmuk_claimNextChildId(),
				type,
				code
		);
	}

	public static KeyMapping createAlternativeKeyBindingWithName(KeyMapping base, String name, InputConstants.Key key) {
		return createAlternativeKeyBindingWithName(base, name, key.getType(), key.getValue());
	}

	public static KeyMapping createAlternativeKeyBindingWithName(
			KeyMapping base,
			String name,
			InputConstants.Type type,
			int code
	) {
		IKeyBinding parent = (IKeyBinding) base;
		KeyMapping alt = new AlternativeKeyBinding(base, name, type, code, base.getCategory());
		parent.nmuk_addAlternative(alt);
		return alt;
	}

	//# if MC_VERSION_NUMBER >= 11800
	public static List<KeyBindsList.Entry> getControlsListWidgetEntries() {
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof KeybindsScreenAccessor) {
			//noinspection unchecked
			return (List<KeyBindsList.Entry>) (Object)
					((EntryListWidgetAccessor) ((KeybindsScreenAccessor) screen).getKeyBindsList()).getChildren();
		}
		return new ArrayList<>();
	}

	public static KeyBindsList.KeyEntry createKeyBindingEntry(KeyBindsList listWidget, KeyMapping binding, Component text) {
		try {
			Constructor<KeyBindsList.KeyEntry> constructor = KeyBindsList.KeyEntry.class
					.getDeclaredConstructor(KeyBindsList.class, KeyMapping.class, Component.class);
			constructor.setAccessible(true);
			return constructor.newInstance(listWidget, binding, text);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			NMUK.log(Level.ERROR, "Failed to create GUI representation of key binding", e);
		}
		return null;
	}
	//# else
	//- public static List<ControlList.Entry> getControlsListWidgetEntries() {
	//- 	Screen screen = Minecraft.getInstance().screen;
	//- 	if (screen instanceof KeybindsScreenAccessor) {
	//- 		//noinspection unchecked
	//- 		return (List<ControlList.Entry>) (Object)
	//- 				((EntryListWidgetAccessor) ((KeybindsScreenAccessor) screen).getControlList()).getChildren();
	//- 	}
	//- 	return new ArrayList<>();
	//- }

	//- public static ControlList.KeyEntry createKeyBindingEntry(ControlList listWidget, KeyMapping binding, Component text) {
	//- 	try {
	//- 		Constructor<ControlList.KeyEntry> constructor = ControlList.KeyEntry.class
	//- 				.getDeclaredConstructor(ControlList.class, KeyMapping.class, Component.class);
	//- 		constructor.setAccessible(true);
	//- 		return constructor.newInstance(listWidget, binding, text);
	//- 	} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
	//- 		NMUK.log(Level.ERROR, "Failed to create GUI representation of key binding", e);
	//- 	}
	//- 	return null;
	//- }
	//# end
}
