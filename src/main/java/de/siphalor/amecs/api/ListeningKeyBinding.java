package de.siphalor.amecs.api;

/**
 * An interface to be used on {@link net.minecraft.client.options.KeyBinding}s. It triggers everytime the user presses the specified keys.
 *
 * @deprecated Use 1.15's introduced setPressed method as handler instead (or {@link AmecsKeyBinding#onPressed()} for convenience)
 */
@Deprecated
public interface ListeningKeyBinding {
	/**
	 * This method gets called when the user triggers this key binding
	 */
	void onPressed();
}
