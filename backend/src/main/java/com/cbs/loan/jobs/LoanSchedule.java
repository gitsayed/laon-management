package com.cbs.loan.jobs;


import com.cbs.loan.dto.LoanStatus;
import com.cbs.loan.entity.Loan;
import com.cbs.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class LoanSchedule {

    @Value("${lms.loanStatusScheduleIntervalInHour}")
    private final long loanStatusScheduleIntervalInHour = 24;
    private final LoanRepository loanRepository;

    @Scheduled(fixedDelay = loanStatusScheduleIntervalInHour, timeUnit = TimeUnit.HOURS)
    public void checkLoanStatusEvery24Hours() {
        log.info("Schedule called: checkLoanStatusEvery24Hours...");
        List<Loan> loanList = loanRepository.findByNextDueDateLessThanAndStatusNot(LocalDate.now(), LoanStatus.DEFAULTED);
        if (!loanList.isEmpty()) {
            loanList = loanList.stream().map(item -> item.setStatus(LoanStatus.DEFAULTED)).toList();
            loanList = loanRepository.saveAll(loanList);
            log.info("Loan Defaulted: {}", loanList.size());
        }

    }


}
