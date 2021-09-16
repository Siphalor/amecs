package de.siphalor.amecs.keybinding;

import de.siphalor.amecs.VersionedLogicMethodHelper.ReflectionExceptionProxiedMethod;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.api.input.InputEventHandler;
import de.siphalor.amecs.impl.duck.IKeyBinding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HotbarScrollKeyBinding extends AmecsKeyBinding implements InputEventHandler {
	@SuppressWarnings("unused") // used via reflection
	private static final String Method_scrollLogic_PREFIX = "scrollLogic$";
	private static ReflectionExceptionProxiedMethod Method_scrollLogic;

	public static double SCROLL_SPEED = 1;
	// vanilla updates directly on the scroll callback. We do it on the handleInputEvent method to ensure a usual state when evaluating this keybinding event
	// because we might get trigged from a keyboard key when binding is changed
	public static double SCROLL_SPEED_LIMIT = Double.POSITIVE_INFINITY;

	public final boolean scrollUp;

	public HotbarScrollKeyBinding(String id, InputUtil.Type type, int code, String category, KeyModifiers defaultModifiers, boolean scrollUp) {
		super(id, type, code, category, defaultModifiers);
		this.scrollUp = scrollUp;
	}

	// TODO: check if it is really equal for all versions between 1.8 - 1.17.1
	// from minecraft code: Mouse
	@SuppressWarnings("unused") // used via reflection
	private void scrollLogic$1_8(MinecraftClient client, int scrollCount) {
		if (client.player.isSpectator()) {
			if (client.inGameHud.getSpectatorHud().isOpen()) {
				client.inGameHud.getSpectatorHud().cycleSlot(-scrollCount);
			} else {
				float h = MathHelper.clamp(client.player.getAbilities().getFlySpeed() + scrollCount * 0.005F, 0.0F, 0.2F);
				client.player.getAbilities().setFlySpeed(h);
			}
		} else {
			client.player.getInventory().scrollInHotbar(scrollCount);
		}
	}

	// TODO: copy the byteCode from Mouse in order to remove this version check
	private void scrollLogic_currentVersion(MinecraftClient client, int scrollCount) {
		Method_scrollLogic.invoke(this, client, scrollCount);
	}

	@Override
	public void handleInput(MinecraftClient client) {
		int scrollCount = ((IKeyBinding) this).amecs$getTimesPressed();
		((IKeyBinding) this).amecs$setTimesPressed(0);

		scrollCount = (int) Math.min(SCROLL_SPEED * scrollCount, SCROLL_SPEED_LIMIT);

		scrollCount = scrollUp ? scrollCount : -scrollCount;

		// this is really necessary. Removing this will lead to an infinity loop when in spectator mode and using the command hotbar
		if (scrollCount == 0) {
			return;
		}

		scrollLogic_currentVersion(client, scrollCount);
	}
}
