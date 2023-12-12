# AutoTreeChopper Demo Task by James Gardner

This took me around 5 hours to complete, probably a little less. It went fairly smoothly and I didn't run into any major hiccups.

The majority of the time was spent on the smooth movement and following the terrain.

Most of the code can be found in the ``TreeChopper`` class. It uses Spigot API 1.20.2

[Demo Video](https://youtu.be/h-eotB-tgiU)

## Features

- Tree chopper can be placed facing any direction, and will continue in that direction following the terrain, unless it hits any of the obstacles (ferns, mushrooms, flowers by default but can be configured in config.yml) or a two block elevation change.
- Whitelist of blocks that can be passed through in config.yml (by default, grass and saplings)
- Three types of tree choppers, with diamond, gold, and emerald blocks. All features and blocks can be configured in config.yml. By default:
    - Diamond Block: Fast and durable, but has low yield.
    - Gold Block: Slow and not durable, but has high yield.
    - Emerald Block: Medium speed and durable, low yield, but replants saplings.
- Time to chop each tree is dependent on the number of logs and the speed of the chopper.
- Tree choppers have durability, and when the durability runs out you will loose the ore block but not the chest or blocks in the chest.
- When the chest is full, the tree chopper will notify the player and stop, dropping any items that cannot fit on the floor.
- Tree chopper will speed up when it comes in contact with a wet sponge or ice.

## Development Process / Challenges

When I read this project, my initial fear was with how I was going to detect trees. I ended up recursively checking a 3x3 cube centered on a log, that then checks a 3x3 around any logs it finds, ect. ect. This approach worked well and it almost always perfectly can chop down any variety of tree. Occasionally there will be a very close tree nearby that it ends up chopping, but I have only seen that once. I decided to not destroy leaves, as this becomes much more difficult as it is hard if not impossible to tell which tree any given leaf belongs to.

The next challenge was how to make the movement smooth. I decided to use a combination of armor stands and falling blocks to be able to smoothly update the location each tick.

The rest of development was fairly straight forward, as it was just implementing the additional features and config.