package fr.izeleam.utils.kscoreboard;

import fr.izeleam.utils.kscoreboard.exceptions.NameOutOfRangeException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class KScoreboardTeam {

  private String name;
  private String displayName;
  private final ChatColor color;
  private final List<UUID> players = new ArrayList<>();
  private final KScoreboard scoreboard;

  public KScoreboardTeam(String name, String displayName, ChatColor color, KScoreboard scoreboard) {
    this.name = name;
    this.displayName = displayName;
    this.color = color;
    this.scoreboard = scoreboard;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (name.length() > 16) {
      throw new NameOutOfRangeException(name);
    }

    this.name = name;
    refresh();
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
    refresh();
  }

  public void refresh() {
    if (this.scoreboard instanceof KPerPlayerScoreboard) {
      for (UUID uuid : this.players) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
          refresh(player.getScoreboard());
        }
      }
    } else {
      refresh(((KGlobalScoreboard) this.scoreboard).toBukkitScoreboard());
    }
  }

  public void refresh(Scoreboard scoreboard) {
    Team team = toBukkitTeam(scoreboard);

    if (team == null) return;

    for (UUID entityUUID : players) {
      Player player = Bukkit.getPlayer(entityUUID);

      if (player != null && !team.hasEntry(player.getName())) {
        team.addEntry(player.getName());
      } else if (player == null) {
        if (!team.hasEntry(entityUUID.toString())) {
          team.addEntry(entityUUID.toString());
        }
      }
    }

    team.setPrefix(ChatColor.translateAlternateColorCodes('&', getDisplayName()));
  }

  private void handleRemoval(String entry) {
    if (scoreboard instanceof KGlobalScoreboard) {
      Team team = toBukkitTeam(((KGlobalScoreboard) scoreboard).toBukkitScoreboard());
      if (team == null) return;
      team.removeEntry(entry);
      return;
    }

    KPerPlayerScoreboard perPlayerScoreboard = (KPerPlayerScoreboard) scoreboard;
    for (UUID scoreboardPlayer : perPlayerScoreboard.getActivePlayers()) {
      Player player = Bukkit.getPlayer(scoreboardPlayer);
      if (player == null) continue;
      Scoreboard playerScoreboard = perPlayerScoreboard.toBukkitScoreboard(player);

      Team team = toBukkitTeam(playerScoreboard);
      if (team == null) continue;
      team.removeEntry(entry);
    }
  }

  public Team toBukkitTeam(Scoreboard bukkitScoreboard) {
    if (bukkitScoreboard == null) return null;

    Team team;

    if (bukkitScoreboard.getTeam(name) != null) {
      team = bukkitScoreboard.getTeam(name);
    } else {
      team = bukkitScoreboard.registerNewTeam(name);
    }

    return team;
  }

  public void addPlayer(Player player) {
    addPlayer(player.getUniqueId());
  }

  public void addPlayer(UUID uuid) {
    if (players.contains(uuid)) return;
    players.add(uuid);

    refresh();
  }

  public void removePlayer(Player player) {
    if (!isOnTeam(player.getUniqueId())) return;

    handleRemoval(player.getName());
  }

  public void removePlayer(UUID uuid) {
    players.remove(uuid);

    refresh();
  }

  protected void destroy() {
    if (this.scoreboard instanceof KPerPlayerScoreboard) {
      for (UUID uuid : this.players) {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
          Team team = player.getScoreboard().getTeam(name);
          if (team != null) team.unregister();
        }
      }
    } else {
      Scoreboard scoreboard = ((KGlobalScoreboard) this.scoreboard).toBukkitScoreboard();
      Team team = scoreboard.getTeam(name);
      if (team != null) team.unregister();
    }

    players.clear();
  }

  public KScoreboard getScoreboard() {
    return scoreboard;
  }

  public List<UUID> getEntities() {
    return players;
  }

  public boolean isOnTeam(UUID uuid) {
    return getEntities().contains(uuid);
  }
}