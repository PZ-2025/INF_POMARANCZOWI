package com.orange.bookmanagment.loan.model.enums;

/**
 * Enum representing the status of a loan.
 * <p>
 * The possible statuses are:
 * <ul>
 *     <li>ACTIVE: The loan is currently active.</li>
 *     <li>OVERDUE: The loan is overdue.</li>
 *     <li>RETURNED: The loan has been returned.</li>
 *     <li>LOST: The loaned item is lost.</li>
 * </ul>
 */
public enum LoanStatus {
    ACTIVE,
    OVERDUE,
    RETURNED,
    LOST
}
