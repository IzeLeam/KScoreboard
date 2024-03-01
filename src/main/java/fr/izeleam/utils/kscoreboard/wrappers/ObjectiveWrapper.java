package fr.izeleam.utils.kscoreboard.wrappers;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class ObjectiveWrapper {

  public Objective getNameHealthObjective(Scoreboard scoreboard) {
    Objective healthObjective = scoreboard.getObjective("nameHealth");
    if (healthObjective == null) {
      healthObjective = scoreboard.registerNewObjective("nameHealth", "health");
      healthObjective.setDisplayName("§c❤");
    }
    return healthObjective;
  }

  public Objective getTabHealthObjective(HealthStyle healthStyle, Scoreboard scoreboard) {
    Objective healthObjective = scoreboard.getObjective("tabHealth");
    if (healthObjective == null) {
      healthObjective = scoreboard.registerNewObjective(
          "tabHealth",
          "health"
      );
      healthObjective.setDisplayName("health");
    }
    return healthObjective;
  }

  public Objective getSecondObjective(Scoreboard scoreboard) {
    Objective objective = scoreboard.getObjective("second");
    if (objective == null) {
      objective = scoreboard.registerNewObjective("second", "second");
    }
    return objective;
  }
}
