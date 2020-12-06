# CrossStitch

CrossStitch is a Minecraft mod designed to improve Minecraft proxy compatibility with other Minecraft mods.
This mod is supported by the [Velocity](https://velocitypowered.com/) team and is developed against Velocity
and Fabric, but the principles used are intended to be generic enough to apply to any Minecraft proxy and to
any Minecraft modding platform. (Once we have modern Forge support in Velocity, for instance, we'll have a version
for Forge, for instance.)

## Installation

Download the mod from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/crossstitch) or from
the releases section here.

CrossStitch needs to be installed on the Fabric server itself - Velocity supports CrossStitch as
of Velocity 1.1.2.

Once installed, make sure to only use Velocity to connect to your server, as CrossStitch isn't available
client-side - it's designed to work only with Velocity.

## Why?

Mojang has been increasing the opportunities that mod developers have to add new content to the game.
Unfortunately, some of those improvements do conflict with traditional Minecraft proxies. CrossStitch
is our attempt to improve compatibility.

## Features

* Allows custom Brigadier argument types to be used
  * Previously, this would have not been possible, _or_ we would have had to write an argument serializer to
    cover every mod ever, which is not sustainable. CrossStitch implements a generic approach that will work
    with every mod and in theory every proxy as well.
* More as the need arises.
