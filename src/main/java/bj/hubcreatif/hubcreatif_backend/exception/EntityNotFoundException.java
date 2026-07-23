package bj.hubcreatif.hubcreatif_backend.exception;

public class EntityNotFoundException extends RuntimeException{
    private String entity;

    public EntityNotFoundException(String entity, String message) {
        super(message);
        this.entity =entity;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
