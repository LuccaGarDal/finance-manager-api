package com.lucca.finance_manager_api.controller;

import com.lucca.finance_manager_api.dto.ApiResponseDTO;
import com.lucca.finance_manager_api.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponseDTO> getDashboard () {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.ok(dashboardService.getTotalBalance()));
    }


}
