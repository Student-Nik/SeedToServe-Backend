package com.seedtoserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminLoginResponse {

	private String token;
    private Long adminId;
    private String email;
    private String role;
}
