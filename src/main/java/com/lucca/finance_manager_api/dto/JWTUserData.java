package com.lucca.finance_manager_api.dto;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String email) {

}
