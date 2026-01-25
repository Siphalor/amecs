/*
 * Copyright 2026 Siphalor
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

package de.siphalor.amecs.impl;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@CustomLog
public class AmecsLegacyDeprecationEmitter {
	private static final ReportingLevel REPORTING_LEVEL;
	private static final String MESSAGE =
			"Amecs API Legacy Implementation is deprecated, but still being used. "
					+ "Support will be dropped in 2027, please see "
					+ "https://github.com/Siphalor/amecs/blob/cross-version/amecs-api-legacy/MIGRATION.md";

	private static int reports;

	static {
		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		if (now.isBefore(LocalDate.of(2026, 1, 1).atStartOfDay(ZoneOffset.UTC))) { // TODO: feb
			REPORTING_LEVEL = ReportingLevel.DISABLED;
		} else if (now.isBefore(LocalDate.of(2026, 5, 1).atStartOfDay(ZoneOffset.UTC))) {
			REPORTING_LEVEL = ReportingLevel.WARN;
		} else if (now.isBefore(LocalDate.of(2026, 8, 1).atStartOfDay(ZoneOffset.UTC))) {
			REPORTING_LEVEL = ReportingLevel.WARN_MORE;
		} else {
			REPORTING_LEVEL = ReportingLevel.ERROR_MORE;
		}
		log.debug("Amecs Legacy Deprecation Reporting Level set to {}", REPORTING_LEVEL);
	}

	public static void invoke() {
		if (reports < REPORTING_LEVEL.maxReports) {
			reports++;

			Exception e = new RuntimeException();
			if (REPORTING_LEVEL == ReportingLevel.ERROR_MORE) {
				log.error("{}", MESSAGE, e);
			} else {
				log.warn("{}", MESSAGE, e);
			}
		}
	}

	@RequiredArgsConstructor
	enum ReportingLevel {
		DISABLED(0),
		WARN(1),
		WARN_MORE(10),
		ERROR_MORE(10),
		;
		private final int maxReports;
	}
}
