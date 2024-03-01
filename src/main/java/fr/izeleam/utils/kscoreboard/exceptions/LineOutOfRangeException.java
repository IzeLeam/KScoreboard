package fr.izeleam.utils.kscoreboard.exceptions;

public class LineOutOfRangeException extends RuntimeException {
    public LineOutOfRangeException(String line, int maxLength) {
        super("Tried to register aline with a length greater than " + maxLength + " (" + line.length() + "). \n Line content : " + line);
    }
}
