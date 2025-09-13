<div align="center">
<img alt="Logo" src="src/main/resources/assets/amecsapi/icon.png" />

# Amecs API (or Amecs' API)

![supported Minecraft versions: 1.16 | 1.17 | 1.18 | 1.19 | 1.20 | 1.21](https://img.shields.io/badge/support%20for%20MC-1.14%20%7C%201.15%20%7C%201.16%20%7C%201.17%20%7C%201.18%20%7C%201.19%20%7C%201.20%20%7C%201.21-%2356AD56?style=for-the-badge)

[![latest maven release](https://img.shields.io/maven-metadata/v?color=0f9fbc&metadataUrl=https%3A%2F%2Fmaven.siphalor.de%2Fde%2Fsiphalor%2Famecs-api/amecs-api-mc1.21.9%2Fmaven-metadata.xml&style=flat-square)](https://maven.siphalor.de/de/siphalor/amecs-api/)

This library mod provides modifier keys for Minecraft keybindings as well as some related keybinding utilities

**&nbsp;
[Amecs](https://github.com/Siphalor/amecs) ·
[Discord](https://discord.gg/6gaXmbj)
&nbsp;**

</div>

## Usage
 If you're not a modder you're probably looking for [Amecs](https://github.com/Siphalor/amecs).
 
 If you **are** a modder then you can use Amecs with the following in your build.gradle:
 
 ```groovy
repositories {
    maven {
        url "https://maven.siphalor.de/"
        name "Siphalor's Maven"
    }
}

// VERSION: Check the badge above for the latest version
// MINECRAFT_VERSION: The major Minecraft version you're developing for (e.g. 1.15 for 1.15.2)
dependencies {
    include(modImplementation("de.siphalor:amecsapi-MINECRAFT_VERSION:VERSION"))
}
```

## Features

If you want to see some example of how to use this library, you can take a look at the [testmod](./src/testmod/java/de/siphalor/amecs/testmod).

### Modifier Keys

Modifier keys can be set by the user for all keybindings, including Vanilla ones.

If you want to define a keybinding that uses modifier keys for its default value, you can extend the [`AmecsKeyBinding`](./src/main/java/de/siphalor/amecs/api/AmecsKeyBinding.java) class.

If you want to further integrate this library, check the [`KeyBindingUtils`](./src/main/java/de/siphalor/amecs/api/KeyBindingUtils.java) class.

### Priority Keybindings

Priority keybindings are keybindings that are executed before all other keybindings and are allowed to cancel further evaluation.  
This has various use cases, for example:

- Keybindings that work when not in-game
- Keybindings that work regardless if the chat or a GUI is open

You can make use of priority keybindings by implementing the [`PriorityKeyBinding`](src/main/java/de/siphalor/amecs/api/PriorityKeyBinding.java) interface.

Please always carefully check if your priority keybinding takes action in the right context.  
For example, you oftentimes don't want to execute your priority keybinding when the chat is open.

### Keybinding Descriptions

Keybinding descriptions are a way to provide a description for a keybinding that are shown as a tooltip when hovering over the keybinding in the controls menu.

You can add descriptions by defining translations for the keybinding's translation key with the suffix `.amecsapi.description`.

## License

This mod is licensed under [the Apache 2.0 license](./LICENSE).
