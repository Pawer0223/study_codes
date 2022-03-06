package com.example.jwt_service.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
public class ApiUtils {

    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>(true, response, null);
    }

    public static ApiResult<?> error(String message, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(message, status));
    }

    @Getter @ToString
    public static class ApiError {
        private final String message;
        private final int status;

        ApiError(String message, HttpStatus status) {
            this.message = message;
            this.status = status.value();
        }
    }

    @Getter @AllArgsConstructor
    public static class ApiResult<T> {
        private final boolean success;
        private final T response;
        private final ApiError error;
    }

}