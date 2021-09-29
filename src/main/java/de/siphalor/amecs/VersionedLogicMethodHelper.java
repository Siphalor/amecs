package de.siphalor.amecs;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.logging.log4j.Level;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

@Environment(EnvType.CLIENT)
public class VersionedLogicMethodHelper {

	public static void initLogicMethodsForClasses(List<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			List<MethodFieldAndName> fieldAndNames = getMethodFieldAndNamesForClass(clazz);
			for (MethodFieldAndName fn : fieldAndNames) {
				initLogicMethod(clazz, fn);
			}
		}
	}

	public static class MethodFieldAndName {
		public final Field methodField;
		public final String logicMethodNamePrefix;

		public MethodFieldAndName(Field methodField, String logicMethodNamePrefix) {
			this.methodField = methodField;
			this.logicMethodNamePrefix = logicMethodNamePrefix;
		}
	}

	public static final String LOGIC_METHOD_SEARCH_FIELD_NAME_PREFIX = "Method_";
	public static final String LOGIC_METHOD_SEARCH_FIELD_TARGET_NAME_PREFIX_SUFFIX = "_PREFIX";

	public static List<MethodFieldAndName> getMethodFieldAndNamesForClass(Class<?> clazz) {
		List<MethodFieldAndName> ret = new ArrayList<>();
		for (Field f : clazz.getDeclaredFields()) {
			int modifiers = f.getModifiers();
			if (!Modifier.isStatic(modifiers)) {
				continue;
			}
			String fName = f.getName();
			if (!fName.startsWith(LOGIC_METHOD_SEARCH_FIELD_NAME_PREFIX)) {
				continue;
			}
			if (!ReflectionExceptionProxiedMethod.class.isAssignableFrom(f.getType())) {
				continue;
			}

			// found method field
			// now search for its prefix
			try {
				Field prefixField = clazz.getDeclaredField(fName + LOGIC_METHOD_SEARCH_FIELD_TARGET_NAME_PREFIX_SUFFIX);
				prefixField.setAccessible(true);
				String logicMethodPrefix = (String) prefixField.get(null);
				ret.add(new MethodFieldAndName(f, logicMethodPrefix));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				Amecs.log(Level.WARN, "Found logic method search method in class \"" + clazz.getName() + "\" but no associated logic method name prefix");
				e.printStackTrace();
				continue;
			}
		}
		return ret;
	}

	public static Method getLogicMethod(Class<?> clazz, String methodPrefix) {
		TreeMap<SemanticVersion, Method> methodAndVersions = new TreeMap<>();
		for (Method m : clazz.getDeclaredMethods()) {
			String methodName = m.getName();
			if (methodName.startsWith(methodPrefix)) {
				String versionString = methodName.substring(methodPrefix.length()).replace('_', '.');
				try {
					SemanticVersion version = SemanticVersion.parse(versionString);
					methodAndVersions.put(version, m);
				} catch (VersionParsingException e) {
					Amecs.log(Level.ERROR, "Could not parse semantic version for logic method: " + methodName);
				}
			}
		}
		if (Amecs.SEMANTIC_MINECRAFT_VERSION == null) {
			return methodAndVersions.firstEntry().getValue();
		}
		Entry<SemanticVersion, Method> suitable = methodAndVersions.floorEntry(Amecs.SEMANTIC_MINECRAFT_VERSION);
		if (suitable != null) {
			return suitable.getValue();
		}
		return null;
	}

	public static void initLogicMethod(Class<?> clazz, MethodFieldAndName fieldAndName) {
		Method logicMethod = getLogicMethod(clazz, fieldAndName.logicMethodNamePrefix);
		if (logicMethod == null) {
			throw new IllegalStateException("No \"" + fieldAndName.logicMethodNamePrefix + "\" method available for minecraft Version: " + Amecs.SEMANTIC_MINECRAFT_VERSION.getFriendlyString());
		}
		ReflectionExceptionProxiedMethod proxiedMethod = new ReflectionExceptionProxiedMethod(logicMethod);
		try {
			fieldAndName.methodField.setAccessible(true);
			fieldAndName.methodField.set(null, proxiedMethod);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException("Failed to set the logic method for class: \"" + clazz.getName() + "\"");
		}
	}

	public static class ReflectionExceptionProxiedMethod {
		public final Method method;

		public ReflectionExceptionProxiedMethod(Method method) {
			method.setAccessible(true);
			this.method = method;
		}

		public Object invoke(Object instance, Object... args) {
			try {
				return method.invoke(instance, args);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				Amecs.log(Level.ERROR, "Error while executing: \"" + method.getName() + "\" in class: \"" + method.getDeclaringClass().getName() + "\"");
				e.printStackTrace();
			}
			return null;
		}
	}

}
