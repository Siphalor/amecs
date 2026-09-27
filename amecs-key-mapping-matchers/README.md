<div align="center">
<img alt="Logo" src="../images/core-logo-128.png" />

# Amecs Core - Key Mapping Matchers

![supported Minecraft versions: 1.14 | 1.15 | 1.16 | 1.17 | 1.18 | 1.19 | 1.20 | 1.21 | 26.1](https://img.shields.io/badge/support%20for%20MC-1.14%20%7C%201.15%20%7C%201.16%20%7C%201.17%20%7C%201.18%20%7C%201.19%20%7C%201.20%20%7C%201.21%20%7C%2026.1-%2356AD56?style=for-the-badge)

Allows modders to define custom extra predicates for key mappings

</div>

## About

This library replaces the way key presses are dispatched to key mappings.
Mods can hook into this process to decide whether a key mapping should be active
when its bound key is pressed.

It is the foundation for other Amecs modules, such as [Key Modifiers](../amecs-key-modifiers).

## Usage

### Development Setup

Navigate to [the maven repository](https://maven.siphalor.de/de/siphalor/amecs/amecs-key-mapping-matchers)
and select the correct package for the version of Minecraft you are developing for.
You can then copy the dependency definition from the top of the page.

### Matching Key Mappings

To add a custom predicate for key mappings, register an [`AmecsKeyMappingIsActiveMatcher`](src/main/java/de/siphalor/amecs/key_mapping_matchers/api/AmecsKeyMappingIsActiveMatcher.java)
to the `IS_ACTIVE` event in [`AmecsKeyMappingMatchersApi`](src/main/java/de/siphalor/amecs/key_mapping_matchers/api/AmecsKeyMappingMatchersApi.java).

```java
AmecsKeyMappingMatchersApi.IS_ACTIVE.register(mapping -> {
    if (mapping != myKeyMapping) {
        return AmecsKeyMappingMatchResult.EXACT_MATCH;
    }
    return isMyConditionMet() ? AmecsKeyMappingMatchResult.EXACT_MATCH : AmecsKeyMappingMatchResult.NO_MATCH;
});
```

The matcher is queried whenever a key mapping's bound key is pressed.
It must return one of the following [`AmecsKeyMappingMatchResult`](src/main/java/de/siphalor/amecs/key_mapping_matchers/api/AmecsKeyMappingMatchResult.java)s:

- `NO_MATCH`: The key mapping must not be active.
- `FUZZY_MATCH`: The key mapping matches leniently, e.g., if more keys are pressed than strictly required.
- `EXACT_MATCH`: The key mapping fully matches the current state.

The difference between `EXACT_MATCH` and `FUZZY_MATCH` is subtle:
if any key mapping bound to the pressed key claims an exact match,
only the exact matches are treated as active and all fuzzy matches are ignored.

The results of all registered matchers are combined:
if any matcher returns `NO_MATCH`, the key mapping doesn't match;
otherwise, if any matcher returns `FUZZY_MATCH`, the key mapping matches fuzzily.

Matchers are also re-evaluated when any key is released,
so pressed key mappings get released as soon as they no longer match.

### Listening to Key State Changes

> [!WARNING]
> This API is experimental and may change in the future.

To track the state of arbitrary keys, e.g., for use in your matcher,
register an [`AmecsKeyStateChangeListener`](src/main/java/de/siphalor/amecs/key_mapping_matchers/api/AmecsKeyStateChangeListener.java)
to the `ON_KEY_STATE_CHANGE` event.
It is invoked before the key mappings are updated.

```java
AmecsKeyMappingMatchersApi.ON_KEY_STATE_CHANGE.register((key, pressed) -> {
    if (key.getValue() == GLFW.GLFW_KEY_F1) {
        f1Pressed = pressed;
    }
});
```

## License

This mod is licensed under [the Apache 2.0 license](../LICENSE).
