package fr.izeleam.utils.kscoreboard.exceptions;

public class DuplicateTeamException extends RuntimeException {
    public DuplicateTeamException(String name) {
        super("A team named" + name + " already exists");
    }
}
