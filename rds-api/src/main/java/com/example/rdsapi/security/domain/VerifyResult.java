package com.example.rdsapi.security.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VerifyResult {
    private boolean success;
    private String email;
}
