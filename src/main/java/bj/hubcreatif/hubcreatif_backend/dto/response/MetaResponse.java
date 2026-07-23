package bj.hubcreatif.hubcreatif_backend.dto.response;

import org.springframework.data.domain.Page;

public record MetaResponse(
        Long totalPages,
        Long numberOfElements,
        Long totalElements,
        Long size,
        Long pageNumber,
        boolean hasPrev,
        boolean hasNext,
        boolean isLast,
        boolean isFirst
) {
    public static MetaResponse of() {return null;}
    public static <T> MetaResponse ofPage(Page<T> page) {
        return new MetaResponse(
                (long) page.getTotalPages(),
                (long) page.getNumberOfElements(),
                page.getTotalElements(),
                (long) page.getSize(),
                (long) page.getNumber() + 1,
                page.hasPrevious(),
                page.hasNext(),
                page.isLast(),
                page.isFirst()
        );
    }
}
