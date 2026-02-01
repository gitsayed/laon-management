package com.cbs.loan.repository;

import com.cbs.loan.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> , JpaSpecificationExecutor<Payment> {

    String PAID_AMT_SQL = """
            SELECT 
            COALESCE(SUM(p.amountPaid), 0) 
            FROM Payment p
            WHERE p.loan.id = :loanId
            """;
    @Query(value = PAID_AMT_SQL)
    BigDecimal getTotalPaidByLoanId(@Param("loanId") Long loanId);

}
