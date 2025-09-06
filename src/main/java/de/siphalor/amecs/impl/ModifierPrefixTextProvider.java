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

package de.siphalor.amecs.impl;

import de.siphalor.amecs.api.KeyModifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//- import net.minecraft.network.chat.TextComponent;
//- import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

@Environment(EnvType.CLIENT)
public class ModifierPrefixTextProvider {
	//# if MC_VERSION_NUMBER >= 11900
	private static final Component SUFFIX = Component.literal(" + ");
	private static final Component COMPRESSED_SUFFIX = Component.literal("+");
	//# else
	//- private static final Component SUFFIX = new TextComponent(" + ");
	//- private static final Component COMPRESSED_SUFFIX = new TextComponent("+");
	//# end
	private final String translationKey;

	public ModifierPrefixTextProvider(KeyModifier modifier) {
		this(modifier.getTranslationKey());
	}

	public ModifierPrefixTextProvider(String translationKey) {
		this.translationKey = translationKey;
	}

	protected MutableComponent getBaseComponent(Variation variation) {
		//# if MC_VERSION_NUMBER >= 11900
		return MutableComponent.create(variation.getTranslatableComponent(translationKey));
		//# else
		//- return variation.getTranslatableComponent(translationKey);
		//# end
	}

	public MutableComponent getComponent(Variation variation) {
		MutableComponent text = getBaseComponent(variation);
		if (variation == Variation.COMPRESSED) {
			text.append(COMPRESSED_SUFFIX);
		} else {
			text.append(SUFFIX);
		}
		return text;
	}

	public enum Variation {
		COMPRESSED(".tiny"),
		TINY(".tiny"),
		SHORT(".short"),
		NORMAL("");

		// using this array for the values because it is faster than calling values() every time
		public static final Variation[] VALUES = Variation.values();

		public static final Variation WIDEST = NORMAL;
		public static final Variation SMALLEST = COMPRESSED;

		public final String translateKeySuffix;

		Variation(String translateKeySuffix) {
			this.translateKeySuffix = translateKeySuffix;
		}

		//# if MC_VERSION_NUMBER >= 11900
		public TranslatableContents getTranslatableComponent(String translationKey) {
			return new TranslatableContents(translationKey + translateKeySuffix, null, new Object[0]);
		}
		//# else
		//- public TranslatableComponent getTranslatableComponent(String translationKey) {
		//- 	return new TranslatableComponent(translationKey + translateKeySuffix, null, new Object[0]);
		//- }
		//# end

		public Variation getNextVariation(int amount) {
			int targetOrdinal = ordinal() + amount;
			if (targetOrdinal < 0 || targetOrdinal >= VALUES.length) {
				return null;
			}
			return VALUES[targetOrdinal];
		}

		public Variation getSmaller() {
			return getNextVariation(-1);
		}
	}
}
