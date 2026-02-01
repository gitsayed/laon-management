package com.cbs.loan.entity;

import com.cbs.loan.dto.LoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Table(name = "loans")
@Accessors(chain = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loan {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    private Double principalAmount;
    private Double interestRate;
    private Integer tenureMonths;
    private LocalDate createdDate;

    private Double emiAmount;
    private Double totalAmount;
    private Double remainingBalance;
    private Double interestAmount;
    private Double totalPaid;
    private LocalDate nextDueDate;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;





}

