package de.siphalor.amecs.key_mapping_matchers.api;

import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.KeyMapping;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@CustomLog
public class AmecsKeyMappingMatchersApi {
	public static final Event<AmecsKeyMappingIsActiveMatcher> IS_ACTIVE = EventFactory.createArrayBacked(
			AmecsKeyMappingIsActiveMatcher.class,
			matchers -> mapping -> {
				AmecsKeyMappingMatchResult totalResult = AmecsKeyMappingMatchResult.EXACT_MATCH;
				for (AmecsKeyMappingIsActiveMatcher matcher : matchers) {
					try {
						AmecsKeyMappingMatchResult localResult = matcher.matchIsActive(mapping);
						if (localResult == AmecsKeyMappingMatchResult.NO_MATCH) {
							return AmecsKeyMappingMatchResult.NO_MATCH;
						} else if (totalResult == AmecsKeyMappingMatchResult.EXACT_MATCH
								&& localResult == AmecsKeyMappingMatchResult.FUZZY_MATCH) {
							totalResult = AmecsKeyMappingMatchResult.FUZZY_MATCH;
						}
					} catch (Exception e) {
						log.warn("Exception in is active mapping matcher", e);
					}
				}
				return totalResult;
			}
	);

	@ApiStatus.Experimental
	public static final Event<AmecsKeyStateChangeListener> ON_KEY_STATE_CHANGE = EventFactory.createArrayBacked(
			AmecsKeyStateChangeListener.class,
			listeners -> (key, pressed) -> {
				for (AmecsKeyStateChangeListener listener : listeners) {
					try {
						listener.onChange(key, pressed);
					} catch (Exception e) {
						log.warn("Exception in key state listener", e);
					}
				}
			}
	);

	@ApiStatus.Experimental
	public static final Event<Consumer<KeyMapping>> ON_KEY_MAPPING_REGISTERED = EventFactory.createArrayBacked(
			Consumer.class,
			listeners -> mapping -> {
				for (Consumer<KeyMapping> listener : listeners) {
					try {
						listener.accept(mapping);
					} catch (Exception e) {
						log.warn("Exception in key mapping registration listener", e);
					}
				}
			}
	);
}
