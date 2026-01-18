/*
 * Copyright 2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.siphalor.nmuk;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NMUK {

	public static Logger LOGGER = LogManager.getLogger();

	public static final String MOD_ID = "nmuk";
	public static final String MOD_NAME = "No More Useless Keys";

	public static void log(Level level, String message) {
		LOGGER.log(level, () -> "[" + MOD_NAME + "] " + message);
	}

	public static void log(Level level, String message, Throwable e) {
		LOGGER.log(level, () -> "[" + MOD_NAME + "] " + message, e);
	}
}
