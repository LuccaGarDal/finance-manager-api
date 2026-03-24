package com.lucca.finance_manager_api.dto;

public record ApiResponseDTO<T>(Boolean success, T data) {
    public static <T> ApiResponseDTO<T> ok(T data) { return new ApiResponseDTO<>(true, data);}
}
