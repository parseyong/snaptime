package me.snaptime.common;

public record CommonResponseDto<T>(
        String message,
        T result
) {
    public static <T> CommonResponseDto<T> of(String message, T result) {
        return new CommonResponseDto<>(message, result);
    }
}
