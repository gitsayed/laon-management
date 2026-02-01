package com.cbs.loan.service;


import com.cbs.loan.dto.LoanCreateRequestDto;
import com.cbs.loan.dto.LoanProjection;
import com.cbs.loan.dto.LoanStatus;
import com.cbs.loan.dto.PaymentRequestDto;
import com.cbs.loan.entity.Loan;
import com.cbs.loan.entity.Payment;
import com.cbs.loan.exception.LoanException;
import com.cbs.loan.repository.LoanRepository;
import com.cbs.loan.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final PaymentRepository paymentRepository;

    public LoanServiceImpl(LoanRepository loanRepository,
                           PaymentRepository paymentRepository) {
        this.loanRepository = loanRepository;
        this.paymentRepository = paymentRepository;
    }


    @Override
    @Transactional
    public Loan createLoan(LoanCreateRequestDto request) {
        try {
            Loan loan = processLoanCreateInfo(request);
            loan = loanRepository.save(loan);

            log.info("Loan has been created successfully with id: {}", loan.getId());
            return loan;
        } catch (Exception e) {
            log.error("Loan creating error : {}", e.getMessage());
            throw new LoanException("Loan creating error : " + e.getMessage());
        }

    }

    @Override
    public Page<LoanProjection> getPagedLoans(LoanStatus status, Pageable pageable) {
        return loanRepository.findPagedLoans(status, pageable);
    }

    @Override
    public Map<String, Object> getLoanSummaryByLoanId(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new LoanException("Loan not found by id : " + loanId));
        BigDecimal totalPaid = paymentRepository.getTotalPaidByLoanId(loan.getId());
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", loan.getId());
        summary.put("customerName", loan.getCustomerName());
        summary.put("principalAmount", loan.getPrincipalAmount());
        summary.put("interestRate", loan.getInterestRate());
        summary.put("interestAmount", loan.getInterestAmount());
        summary.put("tenureMonths", loan.getTenureMonths());
        summary.put("status", loan.getStatus());
        summary.put("createdDate", loan.getCreatedDate());
        summary.put("emiAmount", loan.getEmiAmount());
        summary.put("totalPaid", totalPaid);
        summary.put("nextDueDate", loan.getNextDueDate());
        summary.put("remainingBalance", loan.getRemainingBalance());
        return summary;
    }

    @Override
    @Transactional
    public Payment addPayment(PaymentRequestDto request) {
        validatePaymentInput(request);
        Loan loan = loanRepository.findById(request.getLoanId()).orElseThrow(() -> new LoanException("Loan not found by id : " + request.getLoanId()));
        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new LoanException("Loan is not ACTIVE");
        }

        // Mock default check
        if (request.getPaymentDate().isAfter(loan.getNextDueDate())) {
            loan.setStatus(LoanStatus.DEFAULTED);
        }

        loan.setTotalPaid(loan.getTotalPaid()+request.getAmountPaid());
        loan.setRemainingBalance(loan.getRemainingBalance()-request.getAmountPaid());
        if (loan.getRemainingBalance() == 0 ||
                loan.getRemainingBalance() < 0) {
            loan.setRemainingBalance(0.0);
            loan.setStatus(LoanStatus.CLOSED);
        }

        loan.setNextDueDate(loan.getNextDueDate().plusMonths(1));

        loanRepository.save(loan);
        Payment payment = new Payment();
        payment.setLoan(loan)
                .setPaymentDate(request.getPaymentDate())
                .setAmountPaid(request.getAmountPaid());
        return paymentRepository.save(payment);
    }


    private Loan processLoanCreateInfo(LoanCreateRequestDto request) {
        validateLoanInput(request);
        Double emi = getEMI(request);
        Double totalAmount = emi*request.getTenureMonths();
        Double totalInterest = totalAmount-request.getPrincipalAmount();
        Loan loan = new Loan();
        loan.setCustomerName(request.getCustomerName())
                .setPrincipalAmount(request.getPrincipalAmount())
                .setInterestRate(request.getInterestRate())
                .setTenureMonths(request.getTenureMonths())
                .setCreatedDate(request.getCreatedDate())
                .setEmiAmount(emi)
                .setInterestAmount(totalInterest)
                .setTotalAmount(totalAmount)
                .setRemainingBalance(totalAmount)
                .setTotalPaid(0.0)
                .setStatus(LoanStatus.ACTIVE)
                .setNextDueDate(request.getCreatedDate().plusMonths(1));

        return loan;
    }


    private static Double getEMI(LoanCreateRequestDto request) {
        Double principal = request.getPrincipalAmount();
        Double annualRate = request.getInterestRate();
        Integer months = request.getTenureMonths();
        Double monthlyRate = annualRate/(12 * 100);

        double onePlusRPowerN = Math.pow(( 1 + monthlyRate), months);
        Double numerator = (principal*monthlyRate)*(onePlusRPowerN);
        Double denominator = onePlusRPowerN-1;
        Double emi = numerator/denominator;
        log.info("emi: {}", emi);
        return emi;
    }


    private void validateLoanInput(LoanCreateRequestDto request) {
        if (request.getCustomerName() == null || request.getCustomerName().isEmpty()) {
            throw new LoanException("customerName cannot be empty");
        }

        if (request.getCreatedDate() == null) {
            throw new LoanException("createdDate cannot be empty");
        }
        if (request.getPrincipalAmount() == null || request.getPrincipalAmount() < 1) {
            throw new LoanException("principalAmount must be positive");
        }
        if (request.getInterestRate() == null || request.getInterestRate() < 1) {
            throw new LoanException("interestRate must be positive");
        }
        if (request.getTenureMonths() == null || request.getTenureMonths() <= 0) {
            throw new LoanException("tenureMonths must be positive");
        }
    }


    private void validatePaymentInput(PaymentRequestDto request) {
        if (request.getLoanId() == null) {
            throw new LoanException("loanId is required");
        }

        if (request.getAmountPaid() == null || request.getAmountPaid() < 0) {
            throw new LoanException("Amount paid must be positive");
        }
        if (request.getPaymentDate() == null) {
            throw new LoanException("Payment date is required");
        }
    }
}
