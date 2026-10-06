package vallegrande.edu.pe.backend.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Constructor con formato conveniente: ResourceNotFoundException("Batch", 42)
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " con id '" + id + "' no fue encontrado");
    }
}
