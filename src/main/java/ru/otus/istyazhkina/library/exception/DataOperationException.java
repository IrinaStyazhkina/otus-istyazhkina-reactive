package ru.otus.istyazhkina.library.exception;

public class DataOperationException extends RuntimeException {

    public DataOperationException() {
    }

    public DataOperationException(String message) {
        super(message);
    }

    public DataOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataOperationException(Throwable cause) {
        super(cause);
    }

    public DataOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
