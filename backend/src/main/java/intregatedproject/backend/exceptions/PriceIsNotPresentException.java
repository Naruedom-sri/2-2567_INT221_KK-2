package intregatedproject.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PriceIsNotPresentException extends RuntimeException {
    public PriceIsNotPresentException(String message) {
        super(message);
    }
}
