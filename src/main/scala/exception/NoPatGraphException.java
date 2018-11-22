package exception;

public final class NoPatGraphException extends Exception {

    private String _msg;

    public NoPatGraphException() {
        super();
    }

    public NoPatGraphException(Exception e) {
        super(e.toString());
        _msg = e.toString();
    }

    public NoPatGraphException(String message) {
        super(message);
        _msg = message;
    }

    public String getMessage() {
        final int col = _msg.indexOf(':');

        if (col > -1)
            return(_msg.substring(col));
        else
            return(_msg);
    }
}