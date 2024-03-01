package fr.izeleam.utils.kscoreboard;

public enum KScoreboardTabHealthStyle {

  NONE, HEARTS, PERCENTAGE, NUMBER;

  public HealthStyle toHealthStyle() {
    switch (this) {
      case HEARTS:
        return HealthStyle.HEARTS;
      case PERCENTAGE:
        return HealthStyle.PERCENTAGE;
      case NUMBER:
        return HealthStyle.NUMBER;
      default:
        return HealthStyle.NONE;
    }
  }
}