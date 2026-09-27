package de.siphalor.amecs.key_mapping_matchers.api;

/**
 * Defines the matching state of the key mapping. <br />
 * The difference between {@link #EXACT_MATCH} and {@link #FUZZY_MATCH} is subtle: <br />
 * If an exact match is claimed, then only other exact matches are treated as active. <br />
 * Whereas fuzzy matches allow for a more lenient matching.
 */
public enum AmecsKeyMappingMatchResult {
	NO_MATCH,
	FUZZY_MATCH,
	EXACT_MATCH,
}
