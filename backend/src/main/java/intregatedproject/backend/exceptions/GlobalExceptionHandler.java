package intregatedproject.backend.exceptions;

import intregatedproject.backend.exceptions.brands.BrandAlreadyExistsException;
import intregatedproject.backend.exceptions.brands.BrandHasSaleItemException;
import intregatedproject.backend.exceptions.users.*;
import intregatedproject.backend.exceptions.verifyEmail.EmailAlreadyVerifiedException;
import intregatedproject.backend.exceptions.verifyEmail.InvalidVerificationTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String error, Object message, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", getTimestamp());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(BrandAlreadyExistsException.class)
    public ResponseEntity<Object> handleBrandAlreadyExists(BrandAlreadyExistsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(BrandHasSaleItemException.class)
    public ResponseEntity<Object> handleBrandHasSaleItem(BrandHasSaleItemException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex,
                                                            HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", fieldErrors, request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<Object> handleInvalidRole(InvalidRoleException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(RequiredFileMissingException.class)
    public ResponseEntity<Object> handleRequiredFileMissing(RequiredFileMissingException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }
    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<Object> handleEmailAlreadyVerified(EmailAlreadyVerifiedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<Object> handleInvalidVerificationToken(InvalidVerificationTokenException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request);
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleAccountIsNotActive(ForbiddenException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), request);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Something went wrong: " + ex.getMessage(),
                request);
    }
}


//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
//        return new ResponseEntity<>(
//                Map.of(
//                        "timestamp",
//                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
//                        "status", 404,
//                        "error", "Not Found",
//                        "message", ex.getMessage(),
//                        "path", request.getRequestURI()),
//                HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(BrandAlreadyExistsException.class)
//    public ResponseEntity<Object> handleBrandAlreadyExists(BrandAlreadyExistsException ex, HttpServletRequest request) {
//        return new ResponseEntity<>(
//                Map.of("timestamp",
//                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
//                        "status", 400,
//                        "error", "Bad Request",
//                        "message", ex.getMessage(),
//                        "path", request.getRequestURI()),
//                HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(BrandHasSaleItemException.class)
//    public ResponseEntity<Object> handleBrandHasSaleItem(BrandHasSaleItemException ex, HttpServletRequest request) {
//        return new ResponseEntity<>(
//                Map.of("timestamp",
//                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
//                        "status", 400,
//                        "error", "Bad Request",
//                        "message", ex.getMessage(),
//                        "path", request.getRequestURI()),
//                HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleGenericException(Exception ex, HttpServletRequest request) {
//        return new ResponseEntity<>(
//                Map.of("timestamp",
//                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
//                        "status", 500,
//                        "error", "Internal Server Error",
//                        "message", "Something went wrong: " + ex.getMessage(),
//                        "path", request.getRequestURI()),
//
//                HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex,
//                                                            HttpServletRequest request) {
//        Map<String, String> fieldErrors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                fieldErrors.put(error.getField(), error.getDefaultMessage()));
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("timestamp", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        body.put("status", HttpStatus.BAD_REQUEST.value());
//        body.put("error", "Bad Request");
//        body.put("message", fieldErrors);
//        body.put("path", request.getRequestURI());
//
//        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
//    }

