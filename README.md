# Join Events

## Configuration

### Ranks

- **enabled**: Whether the rank is enabled.
- **permission**: The permission required to be in the rank.

## Messages Configuration

- **enabled**: Whether custom join and quit messages are enabled.
- **join**: The custom join message format.
- **quit**: The custom quit message format.

## Noise Configuration

- **enabled**: Whether a noise (sound) is played when a player with the MVP rank joins.
- **sound**: The Minecraft sound to play.
- **volume**: The volume of the sound.
- **pitch**: The pitch of the sound.

## Fireworks Configuration

- **enabled**: Whether fireworks are launched when a player with the MVP rank joins.
- **flicker**: Whether the fireworks have a flicker effect.
- **trail**: Whether the fireworks have a trail effect.
- **mainColor**: The color of the main firework explosion.
- **fadeColor**: The color of the fade effect of the firework.
- **type**: The type of the firework.

## Join Book Configuration

- **enabled**: Whether a custom join book is given to players with the MVP rank.
- **title**: The title of the join book.
- **author**: The author of the join book.
- **pages**: The content of the pages in the join book.

## BossBar Configuration

- **enabled**: Whether a custom boss bar is displayed to players with the MVP rank.
- **color**: The color of the boss bar.
- **style**: The style of the boss bar.
- **title**: The title of the boss bar.
- **time**: The time (in seconds) the boss bar is displayed.
- **progress**: The progress (0.0 to 1.0) of the boss bar.

## Join Items Configuration

- **enabled**: Whether a custom item is given to players with the MVP rank.
- **unmoveable**: Whether the item is unmovable in the player's inventory.
- **material**: The material of the custom item.

### NameTag Configuration

- **nbtTagName**: The NBT tag name for the custom item.
- **nbtValue**: The NBT value for the custom item.

### Item Details Configuration

- **name**: The name of the custom item.
- **lore**: The lore (description) of the custom item.
- **slot**: The inventory slot where the custom item is placed.
- **enchantments**: Enchantments applied to the custom item.
- **uses**: The number of uses for the custom item.

### Cooldown Configuration

- **time**: The cooldown time for the custom item.
- **message**: The message displayed when the item is on cooldown.

### Commands Configuration

- **commands**: Custom commands to run when the player right-clicks or left-clicks the custom item, defined as such.
```
  right_click:
    - "give %player_name% 5 diamond"
  left_click:
    - "say %player_name% has just left clicked"
```
