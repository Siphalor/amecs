<div align="center">
<img alt="Logo" src="../images/core-logo-128.png" />

# Amecs Core - Key Mapping Scopes

![supported Minecraft versions: 1.14 | 1.15 | 1.16 | 1.17 | 1.18 | 1.19 | 1.20 | 1.21 | 26.1](https://img.shields.io/badge/support%20for%20MC-1.14%20%7C%201.15%20%7C%201.16%20%7C%201.17%20%7C%201.18%20%7C%201.19%20%7C%201.20%20%7C%201.21%20%7C%2026.1-%2356AD56?style=for-the-badge)

Allows making key mappings only active in certain scopes

</div>

## About

This library allows restricting key mappings to certain scopes, e.g., only in-game or only in inventories.
Key mappings that are bound to the same key but live in different scopes no longer conflict with each other.

It builds upon [Key Mapping Matchers](../amecs-key-mapping-matchers).

## Scopes

The following [`AmecsKeyMappingScope`](src/main/java/de/siphalor/amecs/key_mapping_scopes/api/AmecsKeyMappingScope.java)s are available:

- `IN_GAME`: No screen is open.
- `INVENTORY`: A container screen is open, e.g., the player inventory or a chest.
- `MENU`: Any other screen is open.

By default, key mappings are active in all scopes.

## Usage

### Development Setup

Navigate to [the maven repository](https://maven.siphalor.de/de/siphalor/amecs/amecs-key-mapping-scopes)
and select the correct package for the version of Minecraft you are developing for.
You can then copy the dependency definition from the top of the page.

### Restricting Key Mappings

To restrict a key mapping to certain scopes, pass it to `setScopes` in [`AmecsKeyMappingScopesApi`](src/main/java/de/siphalor/amecs/key_mapping_scopes/api/AmecsKeyMappingScopesApi.java):

```java
AmecsKeyMappingScopesApi.setScopes(myKeyMapping, AmecsKeyMappingScope.IN_GAME, AmecsKeyMappingScope.INVENTORY);
```

The key mapping is then only active while one of the given scopes is current.

## Mod Compatibility

This module may define default scopes for certain modded key mappings.
For example, mappings from [JEI](https://modrinth.com/mod/jei) are by default restricted to the `INVENTORY` scope.

Explicitly setting scopes through the API will always take precedence over these default scopes.

## License

This mod is licensed under [the Apache 2.0 license](../LICENSE).
