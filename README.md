# Smart Feeding Troughs

**Smart Feeding Troughs** automatically feeds and breeds nearby animals in a natural and vanilla-friendly way.

## Features
* Supports both Fabric and NeoForge loaders
* Animals feed only if a nearby eligible mate is also ready to breed, avoiding wasted food.
* Enforces a population cap and stops feeding once the cap is reached.
* Works with any modded animal that extends the vanilla `Animal` class.
* Designed for maximum efficiency by minimizing expensive scanning.
* Supports automated input through top and sides via vanilla hoppers.
* Fully configurable, with command-line and UI options via Cloth Config and Mod Menu.

## How to use:
To craft a Smart Trough you need any wooden slabs and planks, combine them in a crafting table and place the trough. 
Insert eligible food into the 4-slot inventory. i.e. wheat for cows, seeds for chickens, etc.

<img src="https://github.com/kingnhcomcast/Smart-Feeding-Troughs/blob/26.1/media/recipe.png?raw=true" alt="Smart Trough crafting recipe" width="360">

## Future Plans
This mod is being actively developed and the following are the potential planned features and improvements:
* Dynamic rendering of contents showing visual clue on how full the trough is.
* Redstone support: disable via signal, comparator support
* Debugging: visually indicate which animals are claimed by the trough
* If troughs are adjacent, optionally change model to show one long trough (like a vanilla double chest)
* Store XP so the player can collect it later (like a vanilla furnace)
* Feed hurt animals so they heal.
* Feed baby animals so they have a chance to grow faster.
* Improved feeding animation or effects for visual feedback
* Add all wood types
* Config: allow per animal type settings
* Config: whitelist/blacklist animals

## Config Options

* `range`: Maximum distance at which animals can be claimed by the trough. (Default: `16`)
* `maxClaimedAnimals`: Maximum number of animals each trough can claim. Add more troughs to increase the cap further. (Default: `24`)
* `troughClaimCheckInterval`: Number of ticks the trough waits before searching for more animals to claim. (Default: `600`)
* `feedChance`: Probability that an animal will feed when ready. This adds natural variation and can be set to `1.00` for a 100% chance. (Default: `0.25`)

License: MIT

Source: https://github.com/kingnhcomcast/Smart-Breeding-Troughs

Permissions: You are welcome to use this mod in your modpack. If you do, feel free to leave a comment and let me know.

If you want support for other minecraft versions or feature requests, leave a comment!

Enjoy!
