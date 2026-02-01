package com.cbs.loan.controller;


import com.cbs.loan.dto.LoanCreateRequestDto;
import com.cbs.loan.dto.LoanProjection;
import com.cbs.loan.dto.LoanStatus;
import com.cbs.loan.dto.PaymentRequestDto;
import com.cbs.loan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/loans")
    public ResponseEntity<Void> createLoan( @Valid  @RequestBody LoanCreateRequestDto request) {
        log.info("Creating loan --> request {}", request);
        loanService.createLoan(request);
        return  ResponseEntity.ok().build();
    }

    @GetMapping("/loans")
    public ResponseEntity<Page<LoanProjection>> getPagedLoans(@RequestParam(required = false) LoanStatus status,
                                                              Pageable pageable ) {
        log.info("Getting paged loans status: {}", status);
        Page<LoanProjection> page = loanService.getPagedLoans(status, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/loans/{loanId}/summary")
    public ResponseEntity<Map<String, Object>> getLoanSummary(@PathVariable Long loanId) {
        log.info("Getting loan summary by loanId: {}", loanId);
        return ResponseEntity.ok(loanService.getLoanSummaryByLoanId(loanId));
    }

    @PostMapping("/payments")
    public ResponseEntity<Void> addPayment(@RequestBody PaymentRequestDto request) {
        log.info("Adding payment request --> {}", request);
        loanService.addPayment(request);
        return ResponseEntity.ok().build();
    }

}
