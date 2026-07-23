package bj.hubcreatif.hubcreatif_backend.dto.response;

import bj.hubcreatif.hubcreatif_backend.utils.ApiError;

import java.time.LocalDateTime;
import java.util.List;

public class ApiResponse<T> {
    private final LocalDateTime timestamp;
    private boolean success;
    private String message;
    private T data;
    private String path;
    private List<ApiError> errors;
    private int errorCode;

    public ApiResponse() {
        timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> apiSuccess(T data, String path) {
        return new ApiResponse<T>()
                .setSuccess(true)
                .setMessage("Data retrieved successfully.")
                .setData(data)
                .setPath(path);
    }

    public static ApiResponse<?> apiError(String message, String path) {
        return new ApiResponse<>()
                .setSuccess(false)
                .setMessage(message)
                .setPath(path);
    }

    public static <T> ApiResponse<T> apiSuccess(String message, T data, String path) {
        ApiResponse<T> response = apiSuccess(data, path);
        response.setMessage(message);

        return response;
    }

    public static <T> ApiResponse<T> apiSuccess(String message, String path) {
        return apiSuccess(message, null, path);
    }

    public static ApiResponse<?> apiError(List<ApiError> errors, String path) {
        ApiResponse<?> response = apiError("An error occurred.", path);
        response.setErrors(errors);

        return response;
    }

    public static ApiResponse<?> apiError(List<ApiError> errors, String message, String path) {
        ApiResponse<?> response = apiError(message, path);
        response.setErrors(errors);

        return response;
    }

    public static ApiResponse<?> apiError(ApiError error, String message, String path) {
        return apiError(List.of(error), message, path);
    }

    public boolean isSuccess() {
        return success;
    }

    public ApiResponse<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ApiResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public ApiResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getPath() {
        return path;
    }

    public ApiResponse<T> setPath(String path) {
        this.path = path;
        return this;
    }

    public List<ApiError> getErrors() {
        return errors;
    }

    public ApiResponse<T> setErrors(List<ApiError> errors) {
        this.errors = errors;
        return this;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public ApiResponse<T> setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
