package com.cbs.loan.service;

import com.cbs.loan.dto.LoanCreateRequestDto;
import com.cbs.loan.dto.LoanProjection;
import com.cbs.loan.dto.LoanStatus;
import com.cbs.loan.dto.PaymentRequestDto;
import com.cbs.loan.entity.Loan;
import com.cbs.loan.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface LoanService {

    Loan createLoan(LoanCreateRequestDto requestDto);
    Page<LoanProjection> getPagedLoans(LoanStatus status, Pageable pageable);
    Payment addPayment(PaymentRequestDto request);
    Map<String, Object> getLoanSummaryByLoanId(Long id);
}
