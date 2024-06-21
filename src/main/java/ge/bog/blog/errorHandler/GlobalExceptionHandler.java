package ge.bog.blog.errorHandler;

import ge.bog.blog.exceptions.AlreadyExistsException;
import ge.bog.blog.exceptions.DeniedAccessException;
import ge.bog.blog.exceptions.InvalidInputException;
import ge.bog.blog.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandle(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),"An error occurred: " + ex.getMessage(), status.value(),status.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Throwable rootCause = ex.getRootCause() != null ? ex.getRootCause() : ex;
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), "Database error: " + rootCause.getMessage(), status.value(), status.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, status);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        Throwable rootCause = ex.getCause() != null ? ex.getCause() : ex;
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), "Resource not found Exception: " +
                rootCause.getMessage(), status.value(), status.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(DeniedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUserAccessDenied(DeniedAccessException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        Throwable rootCause = ex.getCause() != null ? ex.getCause() : ex;
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), "Access denied: " +
                rootCause.getMessage(), status.value(), status.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, status);
    }
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(InvalidInputException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Throwable rootCause = ex.getCause() != null ? ex.getCause() : ex;
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), "Invalid Input: " +
                rootCause.getMessage(), status.value(), status.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, status);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField())
                .collect(Collectors.joining(", "));

        String validationErrorMessage = "Fields (" + fields + ") mustn't be empty, null or negative";

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Validation Error: " + validationErrorMessage,
                status.value(),
                status.getReasonPhrase()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String validationErrorMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Validation Error: " + validationErrorMessage,
                status.value(),
                status.getReasonPhrase()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ErrorResponse> handleServletRequestBindingException(ServletRequestBindingException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Request Parameter Error: " + "mustn't be null or empty",
                status.value(),
                status.getReasonPhrase()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAdAlreadyExists(AlreadyExistsException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Invalid Input: " + ex.getMessage(),
                status.value(),
                status.getReasonPhrase()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}