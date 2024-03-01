package fr.izeleam.utils.kscoreboard;

import fr.izeleam.utils.kscoreboard.exceptions.LineOutOfRangeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class KPerPlayerScoreboard extends KScoreboard {

  private Function<Player, String> generateTitleFunction;
  private Function<Player, List<String>> generateLinesFunction;
  private final Map<UUID, Scoreboard> playerScoreboardMap = new HashMap<>();

  public KPerPlayerScoreboard(KScoreboardOptions options) {
    setOptions(options);
  }

  public KPerPlayerScoreboard(
      Function<Player, String> generateTitleFunction,
      Function<Player, List<String>> generateLinesFunction,
      KScoreboardOptions options
  ) {
    setOptions(options);

    this.generateTitleFunction = generateTitleFunction;
    this.generateLinesFunction = generateLinesFunction;
  }

  public KPerPlayerScoreboard(
      Function<Player, String> generateTitleFunction,
      Function<Player, List<String>> generateLinesFunction
  ) {
    setOptions(KScoreboardOptions.defaultOptions);

    this.generateTitleFunction = generateTitleFunction;
    this.generateLinesFunction = generateLinesFunction;
  }

  public void updateScoreboard() throws LineOutOfRangeException {
    if (generateLinesFunction == null) return; // Line generator is not ready yet

    for (UUID playerUUID : getActivePlayers()) {
      Player player = Bukkit.getPlayer(playerUUID);
      if (player == null) continue;

      List<String> lines = this.generateLinesFunction.apply(player);
      if (lines == null) {
        lines = new ArrayList<>();
      }
      updateScoreboard(player.getScoreboard(), lines);
    }
  }

  @Override
  public void addPlayer(Player player) {
    getActivePlayers().add(player.getUniqueId());

    Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    player.setScoreboard(scoreboard);
    playerScoreboardMap.put(player.getUniqueId(), scoreboard);

    updateScoreboard();
  }

  @Override
  public void removePlayer(Player player) {
    super.removePlayer(player);

    playerScoreboardMap.remove(player.getUniqueId());
  }

  @Override
  public void setOptions(KScoreboardOptions options) {
    super.setOptions(options);
    updateScoreboard();
  }

  public Scoreboard toBukkitScoreboard(Player player) {
    return playerScoreboardMap.get(player.getUniqueId());
  }

  @Override
  protected String getTitle(Scoreboard scoreboard) {
    return generateTitleFunction.apply(
        playerForScoreboard(scoreboard)
    );
  }

  private Player playerForScoreboard(Scoreboard scoreboard) {
    if (!playerScoreboardMap.containsValue(scoreboard)) return null;
    Player player = null;
    for (Map.Entry<UUID, Scoreboard> entry : playerScoreboardMap.entrySet()) {
      if (entry.getValue().equals(scoreboard)) {
        player = Bukkit.getPlayer(entry.getKey());
        break;
      }
    }
    return player;
  }

  protected void setGenerateLinesFunction(Function<Player, List<String>> generateLinesFunction) {
    this.generateLinesFunction = generateLinesFunction;
  }

  protected void setGenerateTitleFunction(Function<Player, String> generateTitleFunction) {
    this.generateTitleFunction = generateTitleFunction;
  }

}
