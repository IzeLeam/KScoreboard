package fr.izeleam.utils.kscoreboard;

import fr.izeleam.utils.kscoreboard.exceptions.LineOutOfRangeException;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class KGlobalScoreboard extends KScoreboard {

  private Supplier<String> titleSupplier;
  private Supplier<List<String>> linesSupplier;

  private Scoreboard scoreboard;

  protected KGlobalScoreboard(KScoreboardOptions options) {
    this.setOptions(options);
  }

  public KGlobalScoreboard(Supplier<String> titleSupplier, Supplier<List<String>> linesSupplier, KScoreboardOptions options) {
    this.titleSupplier = titleSupplier;
    this.linesSupplier = linesSupplier;
    this.setOptions(options);
  }

  public KGlobalScoreboard(Supplier<String> titleSupplier, Supplier<List<String>> linesSupplier) {
    this.titleSupplier = titleSupplier;
    this.linesSupplier = linesSupplier;
    this.setOptions(KScoreboardOptions.defaultOptions);
  }

  public void updateScoreboard() throws LineOutOfRangeException {
    createBukkitScoreboardIfNull();
    if (linesSupplier != null) updateScoreboard(toBukkitScoreboard(), linesSupplier.get());
  }

  public void addPlayer(Player player) {
    super.addPlayer(player);
    createBukkitScoreboardIfNull();
    player.setScoreboard(scoreboard);
  }

  public void setOptions(KScoreboardOptions options) {
    super.setOptions(options);
    updateScoreboard();
  }

  public Scoreboard toBukkitScoreboard() {
    return scoreboard;
  }

  private void createBukkitScoreboardIfNull() {
    if (this.scoreboard != null) return;

    ScoreboardManager scoreboardManager = Bukkit.getServer().getScoreboardManager();
    if (scoreboardManager == null) return;

    scoreboard = scoreboardManager.getNewScoreboard();

    for (UUID playerUUID : getActivePlayers()) {
      Player player = Bukkit.getPlayer(playerUUID);

      if (player != null) {
        player.setScoreboard(scoreboard);
      }
    }
  }

  protected void setLinesSupplier(Supplier<List<String>> linesSupplier) {
    this.linesSupplier = linesSupplier;
  }

  protected void setTitleSupplier(Supplier<String> titleSupplier) {
    this.titleSupplier = titleSupplier;
  }

  protected String getTitle(Scoreboard scoreboard) {
    return titleSupplier.get();
  }

}
