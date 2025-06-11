package com.example.blockchain.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class EmailVerificationResult {
    private boolean success;
}
