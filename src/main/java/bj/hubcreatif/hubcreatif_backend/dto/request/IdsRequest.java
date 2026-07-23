package bj.hubcreatif.hubcreatif_backend.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Collection;
import java.util.List;

public record IdsRequest(
        @NotEmpty(message = "IDS list can not be empty")
        List<Integer> ids
) {
    public static Collection<Long> ids(Long... list) {
        return List.of(list);
    }

    public static IdsRequest of(Integer... ids) {
        return IdsRequest.builder().ids(ids).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Integer> ids;

        public Builder ids(Integer... ids) {
            this.ids = List.of(ids);
            return this;
        }

        public IdsRequest build() {
            return new IdsRequest(ids);
        }
    }
}
