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
import de.siphalor.nmuk.impl.IKeyBinding;
import de.siphalor.nmuk.impl.NMUKKeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.language.I18n;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Mixin(value = KeyMapping.class, priority = 800)
public abstract class MixinKeyBinding implements IKeyBinding {
	@Shadow
	private boolean isDown;
	@Shadow
	@Final
	private String category;
	@Shadow
	@Final
	private String name;

	@Unique
	private List<KeyMapping> children = null;
	@Unique
	short nextChildId = 0;
	@Unique
	private KeyMapping parent = null;

	@Override
	public short nmuk_getNextChildId() {
		return nextChildId++;
	}

	@Override
	public void nmuk_setNextChildId(short nextChildId) {
		this.nextChildId = nextChildId;
	}

	@Override
	public boolean nmuk_isAlternative() {
		return parent != null;
	}

	@Override
	public KeyMapping nmuk_getParent() {
		return parent;
	}

	@Override
	public void nmuk_setParent(KeyMapping binding) {
		parent = binding;
	}

	@Override
	public List<KeyMapping> nmuk_getAlternatives() {
		return children;
	}

	@Override
	public int nmuk_getAlternativesCount() {
		if (children == null) {
			return 0;
		} else {
			return children.size();
		}
	}

	@Override
	public void nmuk_removeAlternative(KeyMapping binding) {
		if (children != null) {
			children.remove(binding);
		}
	}

	@Override
	public void nmuk_addAlternative(KeyMapping binding) {
		if (children == null) {
			children = new LinkedList<>();
		}
		children.add(binding);
	}

	@Override
	public int nmuk_getIndexInParent() {
		if (parent == null) {
			return 0;
		}
		return ((IKeyBinding) parent).nmuk_getAlternatives().indexOf((KeyMapping) (Object) this);
	}

	@Inject(
			method = "click",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyMapping;clickCount:I"),
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private static void onKeyPressed(InputConstants.Key key, CallbackInfo callbackInfo, KeyMapping binding) {
		KeyMapping parent = ((IKeyBinding) binding).nmuk_getParent();
		if (parent != null) {
			((KeyBindingAccessor) parent).setClickCount(((KeyBindingAccessor) parent).getClickCount() + 1);
			callbackInfo.cancel();
		}
	}

	@Inject(
			method = "isDown",
			at = @At("RETURN"),
			cancellable = true
	)
	public void isPressedInjection(CallbackInfoReturnable<Boolean> cir) {
		if (!isDown && children != null && !children.isEmpty()) {
			for (KeyMapping child : children) {
				if (child.isDown()) {
					cir.setReturnValue(true);
				}
			}
		}
	}

	@Inject(
			method = "release",
			at = @At("RETURN")
	)
	private void resetInjection(CallbackInfo callbackInfo) {
		if (children != null && !children.isEmpty()) {
			for (KeyMapping child : children) {
				child.setDown(false);
			}
		}
	}

	@Inject(
			method = "compareTo(Lnet/minecraft/client/KeyMapping;)I",
			at = @At("HEAD"),
			cancellable = true
	)
	public void compareToInjection(KeyMapping other, CallbackInfoReturnable<Integer> cir) {
		if (parent != null) {
			if (other == parent) {
				cir.setReturnValue(1);
			} else if (category.equals(other.getCategory())) {
				KeyMapping otherParent = ((IKeyBinding) other).nmuk_getParent();
				if (otherParent == parent) {
					cir.setReturnValue(Integer.compare(nmuk_getIndexInParent(), ((IKeyBinding) other).nmuk_getIndexInParent()));
				} else {
					cir.setReturnValue(
							I18n.get(StringUtils.substringBeforeLast(name, "%"))
									.compareTo(I18n.get(StringUtils.substringBeforeLast(other.getName(), "%")))
					);
				}
			}
		}
	}

	@Inject(
			method = "matches",
			at = @At("HEAD"),
			cancellable = true
	)
	public void matchesKeyInjection(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> cir) {
		if (children != null && !children.isEmpty()) {
			for (KeyMapping child : children) {
				if (child.matches(keyCode, scanCode)) {
					cir.setReturnValue(true);
				}
			}
		}
	}

	@Inject(
			method = "matchesMouse",
			at = @At("HEAD"),
			cancellable = true
	)
	public void matchesMouseInjection(int code, CallbackInfoReturnable<Boolean> cir) {
		if (children != null && !children.isEmpty()) {
			for (KeyMapping child : children) {
				if (child.matchesMouse(code)) {
					cir.setReturnValue(true);
				}
			}
		}
	}

	@Inject(
			method = "isDefault",
			at = @At("RETURN"),
			cancellable = true
	)
	public void isDefaultInjection(CallbackInfoReturnable<Boolean> cir) {
		if (parent == null) {
			Collection<KeyMapping> defaults = NMUKKeyBindingHelper.defaultAlternatives.get((KeyMapping) (Object) this);
			if (defaults.isEmpty()) {
				if (children != null && !children.isEmpty()) {
					cir.setReturnValue(false);
				}
			} else {
				if (defaults.size() == children.size()) {
					for (KeyMapping child : children) {
						if (!defaults.contains(child)) {
							cir.setReturnValue(false);
							return;
						}
						if (!child.isDefault()) {
							cir.setReturnValue(false);
							return;
						}
					}
				} else {
					cir.setReturnValue(false);
				}
			}
		}
	}
}
