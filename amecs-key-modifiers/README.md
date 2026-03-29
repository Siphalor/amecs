<div align="center">
<img alt="Logo" src="../images/core-logo-128.png" />

# Amecs Core - Key Modifiers

![supported Minecraft versions: 1.14 | 1.15 | 1.16 | 1.17 | 1.18 | 1.19 | 1.20 | 1.21 | 26.1](https://img.shields.io/badge/support%20for%20MC-1.14%20%7C%201.15%20%7C%201.16%20%7C%201.17%20%7C%201.18%20%7C%201.19%20%7C%201.20%20%7C%201.21%20%7C%2026.1-%2356AD56?style=for-the-badge)

Allows users and modders to define modifier keys for key mappings

</div>

## About

Modifier keys can be set by the user for all key mappings, including Vanilla ones.

## Usage

### Development Setup

Navigate to [the maven repository](https://maven.siphalor.de/de/siphalor/amecs/amecs-key-modifiers)
and select the correct package for the version of Minecraft you are developing for.
You can then copy the dependency definition from the top of the page.

### Defining Key Mappings with Default Modifiers

If you want to define a key mapping that uses modifier keys for its default value, you can use the [`AmecsKeyMappingWithKeyModifiers`](src/main/java/de/siphalor/amecs/key_modifiers/api/AmecsKeyMappingWithKeyModifiers.java) class.

```java
KeyMapping myKeyMapping = new AmecsKeyMappingWithKeyModifiers(
    new ResourceLocation("my-mod", "my_key"),
    InputConstants.Type.KEYSYM,
    GLFW.GLFW_KEY_G,
    Category.MISC,
    new AmecsKeyModifierCombination(AmecsKeyModifiers.CONTROL, AmecsKeyModifiers.ALT)
);
```

### Accessing Modifiers

If you want to interact with the modifiers of a key mapping, use the [`AmecsKeyModifiersApi`](src/main/java/de/siphalor/amecs/key_modifiers/api/AmecsKeyModifiersApi.java) class.

```java
// Get the currently bound modifiers
AmecsKeyModifierCombination boundModifiers = AmecsKeyModifiersApi.getBoundModifiers(keyMapping);

// Check if a specific modifier is set
boolean isControlPressed = boundModifiers.get(AmecsKeyModifiers.CONTROL);

// Get the default modifiers
AmecsKeyModifierCombination defaultModifiers = AmecsKeyModifiersApi.getDefaultModifiers(keyMapping);
```

### Working with Modifier Combinations

The [`AmecsKeyModifierCombination`](src/main/java/de/siphalor/amecs/key_modifiers/api/AmecsKeyModifierCombination.java) class represents a set of modifiers.

```java
// Create a combination
AmecsKeyModifierCombination combo = new AmecsKeyModifierCombination(AmecsKeyModifiers.SHIFT, AmecsKeyModifiers.ALT);

// Check if no modifiers are set
boolean isUnset = combo.isUnset();

// Get currently pressed modifiers
AmecsKeyModifierCombination pressed = AmecsKeyModifierCombination.getCurrentlyPressed();
```

### Custom Modifiers

You can register your own modifiers using the [`AmecsKeyModifiers`](src/main/java/de/siphalor/amecs/key_modifiers/api/AmecsKeyModifiers.java) class.
Note that this must be done during early initialization before the API is sealed.

```java
AmecsKeyModifier myModifier = new DefaultKeyModifier("my_modifier", -1, GLFW.GLFW_KEY_F1);
AmecsKeyModifiers.register(myModifier);
```

## License

This mod is licensed under [the Apache 2.0 license](../LICENSE).
