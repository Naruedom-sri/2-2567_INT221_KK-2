package intregatedproject.backend.exceptions.brand;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BrandHasSaleItemException extends RuntimeException {
    public BrandHasSaleItemException(String message) {
        super(message);
    }
}
