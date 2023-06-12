# Extra Player Renderer

![](logo.png)

[![Quick Fabric Release](https://github.com/LucunJi/ExtraPlayerRenderer/actions/workflows/quick-fabric-release.yml/badge.svg)](https://github.com/LucunJi/ExtraPlayerRenderer/actions/workflows/quick-fabric-release.yml)
![](https://img.shields.io/badge/dynamic/json?color=5da545&label=modrinth&prefix=downloads%20&query=hits.0.downloads&url=https://api.modrinth.com/v2/search?query=extraplayerrenderer&limit=1&index=relevance)
![](https://cf.way2muchnoise.eu/extraplayerrenderer.svg)
![](https://cf.way2muchnoise.eu/versions/extraplayerrenderer.svg)

*If you are looking for the same mod but for Forge, [click here](https://www.curseforge.com/minecraft/mc-mods/extra-player-render). It's not developed by me but still good to use.*


In short, this mod renders a player model in the bottom left corner (by default, configurable) of the screen. Adaptable & configurable. That's it.

![](images/img1.jpg)

## Features

In spectator mode, the rendered model gets automatically changed into the entity that your view is attached to.

![](images/spectator.jpg)

When the player's posture changes, the position of the rendered model changes smoothly.

![](images/transition.webp)

The riding entity can also be rendered, so you can show your favourite horse.

![](images/vehicle.jpg)

## Fully Configurable

By pressing F8 in game (by default, still configurable), the configuration screen will pop up.

Explore the tabs and buttons, there are a lot for you to config: the scale, position and rotation of the figure, the head & body rotations, how to offset(transit) between postures and angle of light on the figure...

![](images/configs.jpg)

## Downloads & Dependencies

Downloading this mod from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/extraplayerrenderer) or [Modrinth](https://modrinth.com/mod/extraplayerrenderer) is recommended, as this can help me gain some rewards.

You can also download from [GitHub](https://github.com/LucunJi/ExtraPlayerRenderer) or [mcmod](https://www.mcmod.cn/class/2374.html).

This mod in all versions, except Minecraft 1.20.0, requires masa's [malilib](https://www.curseforge.com/minecraft/mc-mods/malilib).
The alpha versions for Minecraft 1.20.0 requires [Kosmolot's unofficial port of malilib](https://kosma.pl/masamods-1.20/), as masa has not finished porting.

[Mod Menu](https://modrinth.com/mod/modmenu/) is an optional dependency for the latest versions.

Some earlier versions (now obsolete) requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api).

## Issues
When facing any issue, please first try to update the dependencies (malilib and Fabric Loader),
and then report the bug in [GitHub Issues](https://github.com/LucunJi/ExtraPlayerRenderer/issues) if the issue persists (I rarely look into other places other than GitHub).

This mod is compatible *in theory* with other mods that modifies player's model.
But such compatibility comes from the structure of vanilla Minecraft code, and is *not intended*. Glitches can happen.
You may submit compatibility requests to GitHub Issues.
