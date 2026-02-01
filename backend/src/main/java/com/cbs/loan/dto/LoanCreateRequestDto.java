package com.cbs.loan.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanCreateRequestDto {

    @NotBlank(message = "Customer name is required")
    private String customerName;
    private Double principalAmount;
    private Double interestRate;
    private Integer tenureMonths;
    private LocalDate createdDate;
}
