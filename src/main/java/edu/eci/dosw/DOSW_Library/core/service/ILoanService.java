package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.model.Loan;

import java.util.List;

public interface ILoanService {

    Loan createLoan(String userId, String bookId);

    Loan returnLoan(String loanId);

    Loan getLoanById(String loanId);

    List<Loan> getAllLoans();

    List<Loan> getActiveLoansForUser(String userId);

    List<Loan> getAllLoansForUser(String userId);

    List<Loan> getLoansForBook(String bookId);

    List<Loan> getOverdueLoans();

    List<Loan> getOverdueLoansForUser(String userId);

    long calculateFine(String loanId);

    Loan renewLoan(String loanId, int additionalDays);

    int getTotalActiveLoans();

    int getTotalCompletedLoans();
}
