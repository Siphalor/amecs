<div align="center">
<img alt="Logo" src="../images/core-logo-128.png" />

# Amecs Core - Priority Key Mappings

![supported Minecraft versions: 1.14 | 1.15 | 1.16 | 1.17 | 1.18 | 1.19 | 1.20 | 1.21](https://img.shields.io/badge/support%20for%20MC-1.14%20%7C%201.15%20%7C%201.16%20%7C%201.17%20%7C%201.18%20%7C%201.19%20%7C%201.20%20%7C%201.21-%2356AD56?style=for-the-badge)

Contains a system that allows running key mappings before the usual handling

</div>

## About

Priority keybindings are keybindings that are executed before all other keybindings and are allowed to cancel further evaluation.  
This has various use cases, for example:

- Keybindings that work when not in-game
- Keybindings that work regardless if the chat or a GUI is open

## Usage

You can make use of priority keybindings by implementing the [`PriorityKeyBinding`](../src/main/java/de/siphalor/amecs/api/PriorityKeyBinding.java) interface.

Please always carefully check if your priority keybinding takes action in the right context.  
For example, you oftentimes don't want to execute your priority keybinding when the chat is open.

## License

This mod is licensed under [the Apache 2.0 license](../LICENSE).
