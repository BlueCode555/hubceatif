package bj.hubcreatif.hubcreatif_backend.mappers;

import org.springframework.stereotype.Component;

@Component
public interface Mapper<E, R> {
    R toResponse(E entity);

    default R toForm(E entity) {return null;}
}
