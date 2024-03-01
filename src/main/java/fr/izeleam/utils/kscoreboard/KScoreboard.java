package fr.izeleam.utils.kscoreboard;

import fr.izeleam.utils.kscoreboard.exceptions.DuplicateTeamException;
import fr.izeleam.utils.kscoreboard.exceptions.LineOutOfRangeException;
import fr.izeleam.utils.kscoreboard.exceptions.NameOutOfRangeException;
import fr.izeleam.utils.kscoreboard.wrappers.ObjectiveWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public abstract class KScoreboard {

  private KScoreboardOptions options;

  private ObjectiveWrapper objectiveWrapper;

  private final List<KScoreboardTeam> teams = new ArrayList<>();
  private final List<UUID> activePlayers = new ArrayList<>();

  private final Map<Scoreboard, List<String>> previousEntries = new HashMap<>();

  private final int maxLineLength;

  public KScoreboard() {
    objectiveWrapper = new ObjectiveWrapper();

    maxLineLength = 32;
  }

  public void addPlayer(Player player) {
    if (!activePlayers.contains(player.getUniqueId())) {
      activePlayers.add(player.getUniqueId());
    }
  }

  public void removePlayer(Player player) {
    activePlayers.remove(player.getUniqueId());
    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

    teams.forEach(team -> {
      if (team.isOnTeam(player.getUniqueId())) {
        team.removePlayer(player);
      }
    });
  }

  public Optional<KScoreboardTeam> getTeam(String name) {
    return teams.stream().filter(team -> team.getName().equals(name)).findFirst();
  }

  public KScoreboardTeam createTeam(String name, String displayName) throws DuplicateTeamException, NameOutOfRangeException {
    return createTeam(name, displayName, ChatColor.WHITE);
  }

  public KScoreboardTeam createTeam(String name, String displayName, ChatColor teamColor) throws DuplicateTeamException, NameOutOfRangeException {
    for (KScoreboardTeam team : this.teams) {
      if (ChatColor.stripColor(team.getName()).equalsIgnoreCase(ChatColor.stripColor(name))) {
        throw new DuplicateTeamException(name);
      }
    }

    if (name.length() > 16) {
      throw new NameOutOfRangeException(name);
    }

    KScoreboardTeam team = new KScoreboardTeam(name, displayName, teamColor,this);
    team.refresh();
    this.teams.add(team);
    return team;
  }

  public void removeTeam(KScoreboardTeam team) {
    if (team.getScoreboard() != this) return;

    team.destroy();
    this.teams.remove(team);
  }

  public void destroy() {
    for (UUID playerUUID : activePlayers) {
      Player player = Bukkit.getPlayer(playerUUID);

      if (player != null) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
      }
    }

    for (KScoreboardTeam team : teams) {
      team.destroy();
    }

    this.activePlayers.clear();
    this.teams.clear();
  }

  public List<KScoreboardTeam> getTeams() {
    return teams;
  }

  public KScoreboardOptions getOptions() {
    return options;
  }

  protected void updateScoreboard(Scoreboard scoreboard, List<String> lines) throws LineOutOfRangeException {
    Objective objective = objectiveWrapper.getSecondObjective(scoreboard);

    String title = getTitle(scoreboard);
    if (title == null) {
      title = "";
    }

    objective.setDisplayName(color(title));

    if (lines == null) {
      lines = new ArrayList<>();
    }

    if (previousEntries.containsKey(scoreboard)) {
      if (previousEntries.get(scoreboard).equals(lines)) {
        updateTeams(scoreboard);
        return;
      }

      if (previousEntries.get(scoreboard).size() != lines.size()) {
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        scoreboard.getEntries().forEach(scoreboard::resetScores);
        scoreboard.getTeams().forEach(team -> {
          if (team.getName().contains("line")) {
            team.unregister();
          }
        });
      }
    }

    previousEntries.put(scoreboard, new ArrayList<>(lines));

    List<String> reversedLines = new ArrayList<>(lines);
    Collections.reverse(reversedLines);

    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    Objective healthObjective;

    if (options.getTabHealthStyle() != KScoreboardTabHealthStyle.NONE) {
      healthObjective = objectiveWrapper.getTabHealthObjective(options.getTabHealthStyle().toHealthStyle(), scoreboard);
      healthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    } else {
      healthObjective = objectiveWrapper.getTabHealthObjective(options.getTabHealthStyle().toHealthStyle(), scoreboard);
      healthObjective.unregister();
    }

    if (options.shouldShowHealthUnderName()) {
      healthObjective = objectiveWrapper.getNameHealthObjective(scoreboard);
      healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    } else {
      healthObjective = objectiveWrapper.getNameHealthObjective(scoreboard);
      healthObjective.unregister();
    }

    List<String> colorCodeOptions = colorOptions(reversedLines.size());

    int score = 1;

    for (String entry : reversedLines) {
      if (entry.length() > maxLineLength) {
        throw new LineOutOfRangeException(entry, maxLineLength);
      }

      entry = color(entry);

      Team team = scoreboard.getTeam("line" + score);

      String prefix;
      String suffix = "";

      int cutoff = 16;
      if (entry.length() <= cutoff) {
        prefix = entry;
      } else {
        prefix = entry.substring(0, cutoff);

        if (prefix.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
          prefix = prefix.substring(0, prefix.length() - 1);
          suffix = ChatColor.COLOR_CHAR + suffix;
        }

        suffix = StringUtils.left(ChatColor.getLastColors(prefix) + suffix + entry.substring(cutoff), cutoff);
      }

      if (team != null) {
        team.getEntries().forEach(team::removeEntry);
        team.addEntry(colorCodeOptions.get(score));
      } else {
        team = scoreboard.registerNewTeam("line" + score);
        team.addEntry(colorCodeOptions.get(score));
        objective.getScore(colorCodeOptions.get(score)).setScore(score);
      }

      team.setPrefix(prefix);
      team.setSuffix(suffix);

      score += 1;
    }

    updateTeams(scoreboard);
  }

  private void updateTeams(Scoreboard scoreboard) {
    this.teams.forEach(team -> team.refresh(scoreboard));
  }

  private List<String> colorOptions(int amountOfLines) {
    List<String> colorCodeOptions = new ArrayList<>();
    for (ChatColor color : ChatColor.values()) {
      if (color.isFormat()) {
        continue;
      }

      for (ChatColor secondColor : ChatColor.values()) {
        if (secondColor.isFormat()) {
          continue;
        }

        String option = color + "" + secondColor;

        if (color != secondColor && !colorCodeOptions.contains(option)) {
          colorCodeOptions.add(option);

          if (colorCodeOptions.size() == amountOfLines) break;
        }
      }
    }

    return colorCodeOptions;
  }

  protected List<UUID> getActivePlayers() {
    return activePlayers;
  }

  protected ObjectiveWrapper getObjectiveWrapper() {
    return objectiveWrapper;
  }

  protected void setOptions(KScoreboardOptions options) {
    this.options = options;
  }

  protected String color(String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  protected abstract String getTitle(Scoreboard scoreboard);
}