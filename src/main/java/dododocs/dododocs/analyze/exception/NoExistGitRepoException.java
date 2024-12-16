package dododocs.dododocs.analyze.exception;

public class NoExistGitRepoException extends RuntimeException {
    public NoExistGitRepoException(String message) {
        super(message);
    }
}
