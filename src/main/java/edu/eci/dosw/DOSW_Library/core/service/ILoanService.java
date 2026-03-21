package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.model.Loan;

import java.util.List;

public interface ILoanService {

    Loan createLoan(String userId, String bookId);

    Loan returnLoan(String loanId);

    Loan getLoanById(String loanId);

    List<Loan> getAllLoans();
}
