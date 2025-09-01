package intregatedproject.backend.exceptions.users;

public class RequiredFileMissingException extends RuntimeException {
    public RequiredFileMissingException(String message) {
        super(message);
    }
}
