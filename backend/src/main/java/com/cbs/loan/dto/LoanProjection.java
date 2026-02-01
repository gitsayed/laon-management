package com.cbs.loan.dto;


import java.time.LocalDate;

public interface LoanProjection {
    Long getId();
    String getCustomerName();
    String getStatus();
    Double getPrincipalAmount();
    Double getEmiAmount();
    Double getInterestRate();
    Integer getTenureMonths();
    LocalDate getCreatedDate();
    LocalDate getNextDueDate();

}
