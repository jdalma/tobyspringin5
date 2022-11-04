package springbook.chapter07.sqlService;

public class SqlUpdateFailureException extends RuntimeException {

    public SqlUpdateFailureException() {
    }

    public SqlUpdateFailureException(String message) {
        super(message);
    }
}
