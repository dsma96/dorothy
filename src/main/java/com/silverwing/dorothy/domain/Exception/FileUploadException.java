package com.silverwing.dorothy.domain.Exception;

public class FileUploadException extends ReserveException {
    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
