package com.cbs.loan.repository;


import com.cbs.loan.dto.LoanProjection;
import com.cbs.loan.dto.LoanStatus;
import com.cbs.loan.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {


    String PAGED_LOAN_SQL = """
            SELECT
            l
            FROM Loan l
            WHERE 1=1
            AND (:status IS NULL OR l.status=:status )
            ORDER BY l.id DESC
            """;

    String PAGED_LOAN_SQL_COUNT = """
            SELECT COUNT(DISTINCT l.id)
            FROM Loan l
            WHERE 1=1
            AND (:status IS NULL OR l.status=:status )
            """;

    @Query(value = PAGED_LOAN_SQL, countQuery = PAGED_LOAN_SQL_COUNT)
    Page<LoanProjection> findPagedLoans(@Param("status") LoanStatus status, Pageable pageable);



    List<Loan> findByNextDueDateLessThanAndStatusNot( LocalDate nextDueDate,
                                                         LoanStatus status);

}
