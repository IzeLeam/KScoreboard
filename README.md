# KScoreboard

Welcome to KScoreboard!
This is an easier way to create scoreboards for your server.

Please report any issues you find.

## Features

For the tab list:

- ** Team-based tab list **: Display teams in the tab list.

Two types of sides scoreboards:

- ** Per player scoreboards **: Each player has their own scoreboard.
- ** Global scoreboards **: A single scoreboard for all players.

Health display customization:

- ** None **: No health display.
- ** Hearts **: Display health as hearts.
- ** Numbers **: Display health as numbers.
- ** Percentage **: Display health as a percentage.

## How to use

### Per player scoreboards

```java
import fr.izeleam.utils.kscoreboard.KScoreboardOptions;
import fr.izeleam.utils.kscoreboard.KScoreboardTabHealthStyle;
import org.bukkit.Bukkit;

public class Main extends JavaPlugin {

  @Override
  public void onEnable() {
    // Create a new scoreboard
    KPerPlayerScoreboard scoreboard = new KPerPlayerScoreboard(
        (player) -> {
          "Hello " + player.getName();
        },
        (player) -> {
          Arrays.asList("Line 1", "Line 2", "Line 3");
        },
        new KScoreboardOptions(KScoreboardTabHealthStyle.HEARTS, false)
    );

    Bukkit.getOnlinePlayers().forEach(scoreboard::addPlayer);
  }
}
```

I recommend using a Runnable to update the scoreboard every second if you have dynamic content.

### Global scoreboards

```java
import org.bukkit.Bukkit;

public class Main extends JavaPlugin {

  @Override
  public void onEnable() {
    // Create a new scoreboard
    KGlobalScoreboard scoreboard = new KGlobalScoreboard(
        () -> {
          "Hello everyone!";
        },
        () -> {
          Arrays.asList("Line 1", "Line 2", "Line 3");
        }
    );

    Bukkit.getOnlinePlayers().forEach(scoreboard::addPlayer);
  }
}
```

## Project structure

- ** exceptions **: Custom exceptions.

## Project development

Time spent: 23 hours
Ressources used: Spigot API, Bukkit API, Java 8, Maven, IntelliJ IDEA
Documentation used: https://helpch.at/docs/1.8/index.html?org/bukkit/scoreboard/Scoreboard.html
```
