# Changes
## Rank: 148

The most important factor of the game is distance, therefore I based all the ships on the decisions on the distance it was.

ie. If a planet was closer than an enemy ship it would dock, but if the enemy ship was closer it would attack.

Moreover, I made it so that all ships would only go to empty planets, or planets that weren't full or were not going to be full.
This helped improve the efficiency of each ship, would not be going to a planet that would already be filled, and could focus on
other tasks.

## Issues

Collisions became a major issue with this method, since many ships would be targeting the same closest ships many of my ships would collide.
Abot 10-15% of my ships would collide. Possible fixes could have been implementing a  better navigation method and not moving ships that would 
have otherwise collided.

# Using your starter kit

All starter kits should contain a `run_game.sh` and `run_game.bash`, you can use these scripts to quickly run a game of halite. By default, this script runs the basic __Settler__ bot against itself.

## Bot submission guidelines

Before submitting a bot, make sure you adhere to our guidelines, or the upload or compilation of your bot will fail.

1. You should have a `MyBot.{extension for language}` in the root folder of your zip. For Rust, this should be a `cargo.toml`
2. If you are building on top of starter kit provided by us, make sure to include the hlt folder.

## Uploading your bot

* Website: You can use the [play page](https://halite.io/play-programming-challenge) in the Halite website to submit your bot.
* Halite Client: If you want a command line experience, you can use the [Halite Client tool](https://halite.io/learn-programming-challenge/halite-cli-and-tools/halite-client-tools) to upload your bot.

