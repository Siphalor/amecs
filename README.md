# Amecs

[![curseforge downloads](http://cf.way2muchnoise.eu/full_amecs_downloads.svg)](https://minecraft.curseforge.com/projects/amecs)
[![curseforge mc versions](http://cf.way2muchnoise.eu/versions/amecs.svg)](https://minecraft.curseforge.com/projects/amecs)

![logo](src/main/resources/assets/amecs/logo.png?raw=true)

## API
If you want to use the api provided by this mod you'll want to implement and include this mod:

```groovy
repositories {
    maven { url "https://jitpack.io/" }
}

dependencies {
    modImplementation "com.github.siphalor:amecs:+"
    include "com.github.siphalor:amecs:+"
}
```

If you don't want to shadow this whole mod you may just want to copy the relevant interfaces/classes from `de.siphalor.amecs.api`.

## License

This mod is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
