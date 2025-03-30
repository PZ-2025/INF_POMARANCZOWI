package com.orange.bookmanagment.loan.model.enums;

public enum LoanStatus {

    /**
     * Loan is active and book is currently borrowed
     */
    ACTIVE,

    /**
     * Loan is overdue (due date has passed and book hasn't been returned)
     */
    OVERDUE,

    /**
     * Book has been returned
     */
    RETURNED,

    /**
     * Book has been reported as lost
     */
    LOST

}
