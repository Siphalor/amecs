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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.fabricmc.loader.api.FabricLoader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@CustomLog
public class AmecsLegacyDeprecationEmitter {
	private static final File CONFIG_FILE = FabricLoader.getInstance()
			.getConfigDir()
			.resolve("amecs_legacy_deprecation.properties")
			.toFile();
	private static final ReportingLevel REPORTING_LEVEL;
	private static final String MESSAGE =
			"Amecs API Legacy Implementation is deprecated, but still being used. "
					+ "Support will be dropped in 2027, please see "
					+ "https://github.com/Siphalor/amecs/blob/cross-version/amecs-api-legacy/MIGRATION.md";
	private static boolean shouldWriteConfig;

	private static int reports;

	static {
		ReportingLevel level = null;
		if (CONFIG_FILE.isFile()) {
			Properties properties = new Properties();
			try {
				properties.load(new FileReader(CONFIG_FILE));
				if (properties.containsKey("level")) {
					level = ReportingLevel.valueOf(properties.getProperty("level"));
				}
			} catch (Exception e) {
				log.warn("Failed to read config for Amecs API Legacy's deprecation system", e);
			}
		}
		if (level == null) {
			shouldWriteConfig = true;
			ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
			if (now.isBefore(LocalDate.of(2026, 1, 1).atStartOfDay(ZoneOffset.UTC))) {
				level = ReportingLevel.DISABLED;
			} else if (now.isBefore(LocalDate.of(2026, 5, 1).atStartOfDay(ZoneOffset.UTC))) {
				level = ReportingLevel.WARN;
			} else if (now.isBefore(LocalDate.of(2026, 12, 1).atStartOfDay(ZoneOffset.UTC))) {
				level = ReportingLevel.WARN_MORE;
			} else {
				level = ReportingLevel.ERROR_MORE;
			}
		}
		REPORTING_LEVEL = level;
		log.debug("Amecs Legacy Deprecation Reporting Level set to {}", REPORTING_LEVEL);
	}

	public static void invoke() {
		if (reports < REPORTING_LEVEL.maxReports) {
			reports++;

			Exception e = new RuntimeException();
			if (REPORTING_LEVEL == ReportingLevel.ERROR || REPORTING_LEVEL == ReportingLevel.ERROR_MORE) {
				log.error("{}", MESSAGE, e);
			} else {
				log.warn("{}", MESSAGE, e);
			}

			if (shouldWriteConfig) {
				shouldWriteConfig = false;
				writeConfigFile();
			}
		}
	}

	private static void writeConfigFile() {
		try (Writer writer = new FileWriter(CONFIG_FILE)) {
			writer.write("# Override the reporting level of Amecs Legacy Deprecation warnings.\n"
					+ "# Available options are DISABLED, WARN, WARN_MORE, ERROR, ERROR_MORE\nlevel=");
			writer.write(REPORTING_LEVEL.name());
		} catch (Exception ignored) {}
	}

	@RequiredArgsConstructor
	enum ReportingLevel {
		DISABLED(0),
		WARN(1),
		WARN_MORE(5),
		ERROR(1),
		ERROR_MORE(5),
		;
		private final int maxReports;
	}
}
