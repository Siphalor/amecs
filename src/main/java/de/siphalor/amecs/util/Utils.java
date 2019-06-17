package de.siphalor.amecs.util;

@SuppressWarnings("WeakerAccess")
public class Utils {
	public static char setFlag(char base, char flag, boolean val) {
		return val ? setFlag(base, flag) : removeFlag(base, flag);
	}

	public static char setFlag(char base, char flag) {
		return (char) (base | flag);
	}

	public static char removeFlag(char base, char flag) {
		return (char) (base & (~flag));
	}
}
