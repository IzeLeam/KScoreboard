package fr.izeleam.utils.kscoreboard;

public class KScoreboardOptions {

  private KScoreboardTabHealthStyle tabHealthStyle;
  private boolean showHealthUnderName;

  public KScoreboardOptions(KScoreboardTabHealthStyle tabHealthStyle, boolean showHealthUnderName) {
    this.tabHealthStyle = tabHealthStyle;
    this.showHealthUnderName = showHealthUnderName;
  }

  public static KScoreboardOptions defaultOptions = new KScoreboardOptions(KScoreboardTabHealthStyle.NONE, false);

  public KScoreboardTabHealthStyle getTabHealthStyle() {
    return tabHealthStyle;
  }

  public boolean shouldShowHealthUnderName() {
    return showHealthUnderName;
  }

  public void setShowHealthUnderName(boolean showHealthUnderName) {
    this.showHealthUnderName = showHealthUnderName;
  }

  public void setTabHealthStyle(KScoreboardTabHealthStyle tabHealthStyle) {
    this.tabHealthStyle = tabHealthStyle;
  }
}