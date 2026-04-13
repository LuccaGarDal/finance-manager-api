package com.lucca.finance_manager_api.dto.auth;

import java.util.Map;

public record LoginResponseDTO (Map<String, String> tokens){
}
