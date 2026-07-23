package bj.hubcreatif.hubcreatif_backend.utils;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public record ApiError(
        String field,
        String detail,
        Object rejectedValue,
        String code,
        String formRequest
) {
    public static ApiError of(String field, String detail) { return new ApiError( field, detail, null, null, null);}
    public static ApiError of(ObjectError objectError) {
        FieldError error = (FieldError) objectError;
        return new ApiError(error.getField(), error.getDefaultMessage(), error.getRejectedValue(), error.getCode(), error.getObjectName());
    }
}
