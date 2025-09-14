package de.siphalor.amecs.impl.compat.controlling;

//- import com.blamejared.controlling.api.event.ControllingEvents;
//- import de.siphalor.amecs.api.KeyBindingUtils;
//- import de.siphalor.amecs.api.KeyModifier;
//- import de.siphalor.amecs.impl.KeyBindingEditGuiHelper;

//# if CONTROLLING_INTEGRATION
//- public class AmecsControllingIntegration {
//- 	public static void initialize() {
//- 		ControllingEvents.IS_KEY_CODE_MODIFIER_EVENT.register(
//- 				event -> KeyModifier.fromKey(event.key()) != null
//- 		);
//- 		ControllingEvents.SET_TO_DEFAULT_EVENT.register(event -> {
//- 			KeyBindingUtils.resetBoundModifiers(event.mapping());
//- 			return false;
//- 		});
//- 		ControllingEvents.SET_KEY_EVENT.register(event -> {
//- 			KeyBindingEditGuiHelper.handleKeyPress(event.mapping(), event.key());
//- 			return true;
//- 		});
//- 	}
//- }
//# end
