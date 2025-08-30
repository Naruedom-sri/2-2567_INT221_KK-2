package intregatedproject.backend.exceptions.user;

public class RequiredFileMissingException extends RuntimeException {
    public RequiredFileMissingException(String message) {
        super(message);
    }
}
