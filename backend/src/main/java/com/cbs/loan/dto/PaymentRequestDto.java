package com.cbs.loan.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentRequestDto {

    private Long loanId;
    private Double amountPaid;

    private LocalDate paymentDate;

}
