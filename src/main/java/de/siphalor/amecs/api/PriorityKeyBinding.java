package de.siphalor.amecs.api;

/**
 * An interface to be used on {@link net.minecraft.client.options.KeyBinding}s.
 * <b>This key binding triggers without further conditions before any other checks or conditions.</b>
 */
public interface PriorityKeyBinding {
	/**
	 * This method gets triggered when this key binding matches on an input event. <br>
	 * Since there are no other checks before the invocation you need to check yourself for possible open screens.
	 * @return Return true to cancel propagation of this event. Return false for normal evaluation.
	 */
	boolean onPressedPriority();
}
