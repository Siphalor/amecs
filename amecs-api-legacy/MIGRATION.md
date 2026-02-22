# Migration from the Legacy API

## For Players

If you are a player and have seen a warning/error message about this in the logs, please do the following:

After the line containing this link, there should be the full call stack.
The first line that does not start with `de.siphalor.amecs` should relate to the mod that is causing the issue.

Please report this issue to the mod author.

Until 2027 you won't have to worry about these warnings/errors.
In 2027 newer versions of Amecs might stop working with mods relying on the old API.

## For Mod Developers

Amecs API has been split into various seperate libraries/mods.

This means that you will have to change your dependencies to point to the correct libraries:

- `amecs_key_mapping_descriptions`: Contains the descriptions the key mappings
- `amecs_key_modifiers`: Contains the best known part of Amecs, the ability for users or modders to define key modifiers
- `amecs_mouse_inputs`: Contains additional mouse input codes
- `amecs_priority_key_mappings`: Contains a system that allows running key mappings before the usual handling

While for the time being, the old mod id `amecsapi` will still be provided through a dummy mod,
this will also be removed in 2027.
If you want to react to Amecs functionalities in your mod, you should check for the new mod ids instead.

The migration should mostly be straightforward, as the classes and packages have primarily been renamed
to fit better with Mojang's naming conventions and be more consistent overall.  
See the specific Javadocs on the classes and methods in the legacy module for more details.

Until 2027 a legacy implementation of the old API will continue to be provided by the `amecs_api_legacy` module.
This legacy module will emit warnings/errors when the old API is still used.
