package de.siphalor.amecs.api;

import de.siphalor.amecs.util.IKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

/**
 * A {@link net.minecraft.client.options.KeyBinding} base class to be used when you want to define modifiers keys as default
 */
public class AmecsKeyBinding extends FabricKeyBinding {
	private final KeyModifiers defaultModifiers;

	/**
	 * Constructs a new amecs keybinding
	 * @param id the id to use
	 * @param type the input type which triggers this keybinding
	 * @param code the the default key code
	 * @param category the id of the category which should include this keybinding
	 * @param defaultModifiers the default modifiers
	 */
	protected AmecsKeyBinding(Identifier id, InputUtil.Type type, int code, String category, KeyModifiers defaultModifiers) {
		super(id, type, code, category);
		this.defaultModifiers = defaultModifiers;
	}

	/**
	 * Resets this keybinding (triggered when the user clicks on the "Reset" button).
	 */
	public void resetKeyBinding() {
		((IKeyBinding) this).amecs$getKeyModifiers().setValue(defaultModifiers.getValue());
	}
}
