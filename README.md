# Smart Feeding Troughs

**Smart Feeding Troughs** automatically feeds and breeds nearby animals in a natural and vanilla-friendly way. Also adds an edible Hay Bale Block which allows wheat eating animals to feed directly from the bale.

<p align="center">
  <img src="https://raw.githubusercontent.com/kingnhcomcast/Smart-Feeding-Troughs/refs/heads/26.1/media/FeedingTroughAndHay.gif" width="700"/>
</p>

## Features
* Supports both Fabric and NeoForge loaders
* Shear a Hay Bale and animals that eat wheat can feed directly from the block 8 times.
* Enforces a population cap and stops feeding once the cap is reached.
* Animals feed only if a nearby eligible mate is also ready to breed, avoiding wasted food.
* Works with any modded animal that extends the vanilla `Animal` class. Tested with Alex's Mobs.
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

## Changelog
### 1.1.0
* Added Edible Hay Bale
* Fixed bug where population cap could sometimes be significantly exceeded

---
License: MIT

Source: https://github.com/kingnhcomcast/Smart-Breeding-Troughs

Permissions: You are welcome to use this mod in your modpack. If you do, feel free to leave a comment and let me know.

If you want support for other minecraft versions or feature requests, leave a comment!

Enjoy!
