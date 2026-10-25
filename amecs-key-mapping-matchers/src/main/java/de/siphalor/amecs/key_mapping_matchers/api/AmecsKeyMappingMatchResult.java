/*
 * Copyright 2019 Siphalor
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
