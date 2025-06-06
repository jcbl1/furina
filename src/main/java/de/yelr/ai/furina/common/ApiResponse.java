package de.yelr.ai.furina.common;

/**
 * Envelope used by every JSON endpoint.
 *
 * @param code application-level code, {@code 0} for success
 * @param message human readable description
 * @param data payload, may be {@code null}
 */
public record ApiResponse<T>(int code, String message, T data) {

    public static final int SUCCESS_CODE = 0;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS_CODE, "OK", data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(SUCCESS_CODE, "OK", null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
