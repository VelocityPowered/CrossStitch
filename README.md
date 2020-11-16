# Crossstitch

Crossstitch is a [Fabric](https://fabricmc.net/) mod designed to improve compatibility with Fabric
mods and Minecraft proxies such as  [Velocity](https://velocitypowered.com/). This mod is supported
by the Velocity team and is developed against Velocity, but the principles used are intended to be
generic enough to apply to any Minecraft proxy.

## Installation

Crossstitch only needs to be installed on the Fabric server itself - Velocity supports Crosstitch as
of Velocity 1.1.2.

Once installed, make sure to only use Velocity to connect to your server, as Crossstitch isn't available
client-side - it's designed to work only with Velocity.

## Why?

Mojang has been increasing the opportunities that mod developers have to add new content to the game.
Unfortunately, some of those improvements do conflict with traditional Minecraft proxies. Crossstitch
is our attempt to improve compatibility.

## Features

* Allows custom Brigadier argument types to be used
  * Previously, this would have not been possible, _or_ we would have had to write an argument serializer to
    cover every mod ever, which is not sustainable. Crossstitch implements a generic approach that will work
    with every mod and in theory every proxy as well.
* More as the need arises.