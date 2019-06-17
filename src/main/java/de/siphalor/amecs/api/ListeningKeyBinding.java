package de.siphalor.amecs.api;

/**
 * An interface to be used on {@link net.minecraft.client.options.KeyBinding}s. It triggers everytime the user presses the specified keys.
 */
public interface ListeningKeyBinding {
	/**
	 * This method gets called when the user triggers this key binding
	 */
	void onPressed();
}
