package bj.hubcreatif.hubcreatif_backend.specs;

public record FilterCriteria(
        String field,
        String condition,
        Object value
) {
}
