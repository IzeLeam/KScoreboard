package fr.izeleam.utils.kscoreboard.exceptions;

public class NameOutOfRangeException extends RuntimeException {
    public NameOutOfRangeException(String name) {
        super("Tried to register a name with a length greater than 16 characters (" + name.length() + "). \n Name : " + name);
    }
}
