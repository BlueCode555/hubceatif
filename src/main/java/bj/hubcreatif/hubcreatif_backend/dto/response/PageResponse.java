package bj.hubcreatif.hubcreatif_backend.dto.response;

public record PageResponse<T>(
        T content,
        MetaResponse meta) {

}
