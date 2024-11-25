package com.newproject.exception;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




class FileProcessingExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "File processing error";
        FileProcessingException exception = new FileProcessingException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String errorMessage = "File processing error";
        Throwable cause = new Throwable("Cause of the error");
        FileProcessingException exception = new FileProcessingException(errorMessage, cause);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}