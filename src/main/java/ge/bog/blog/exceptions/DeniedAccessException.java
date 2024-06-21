package ge.bog.blog.exceptions;

public class DeniedAccessException extends RuntimeException{
    public DeniedAccessException(String message) {
        super(message);
    }
}