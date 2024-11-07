package dododocs.dododocs.auth.exception;

public class NoExistMemberException extends RuntimeException {
    public NoExistMemberException(String message) {
        super(message);
    }

    public NoExistMemberException() {
        super("존재하지 않는 멤버입니다.");
    }
}
