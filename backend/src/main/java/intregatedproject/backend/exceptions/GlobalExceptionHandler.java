package intregatedproject.backend.exceptions;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                Map.of(
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                Map.of(
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", "Something went wrong: " + ex.getMessage()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
