
package edu.eci.dosw.DOSW_Library.core.service;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.util.DateUtil;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LoanService {

    private static final int LOAN_DURATION_DAYS = 14;
    static final int MAX_ACTIVE_LOANS = 5;

    private List<Loan> loans;

    private UserService userService;

    private BookService bookService;

    public LoanService() {
        this.loans = new ArrayList<>();
    }

    public Loan createLoan(String userId, String bookId) throws Exception {
        ValidationUtil.validateNotEmpty(userId, "ID del usuario");
        ValidationUtil.validateNotEmpty(bookId, "ID del libro");

        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);


        Loan loan = new Loan();
        loan.setId(IdGeneratorUtil.generateLoanId());
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(DateUtil.now());
        loan.setReturnDate(DateUtil.addDays(DateUtil.now(), LOAN_DURATION_DAYS));
        loan.setStatus(true);

        bookService.decreaseAvailableCopies(bookId);

        loans.add(loan);
        return loan;
    }

    public Loan returnLoan(String loanId) {
        ValidationUtil.validateNotEmpty(loanId, "ID del préstamo");

        Loan loan = getLoanById(loanId);

        if (!loan.isStatus()) {
            throw new IllegalArgumentException("Este préstamo ya ha sido devuelto");
        }

        loan.setStatus(false);
        loan.setActualReturnDate(DateUtil.now());

        bookService.increaseAvailableCopies(loan.getBook().getId());

        return loan;
    }

    public Loan getLoanById(String loanId) {
        ValidationUtil.validateNotEmpty(loanId, "ID del préstamo");

        return loans.stream()
                .filter(loan -> loan.getId().equals(loanId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado con ID: " + loanId));
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    public List<Loan> getActiveLoansForUser(String userId) throws Exception {
        ValidationUtil.validateNotEmpty(userId, "ID del usuario");

        userService.getUserById(userId);

        List<Loan> result = new ArrayList<>();
        loans.forEach(loan -> {
            if (loan.getUser().getId().equals(userId) && loan.isStatus()) {
                result.add(loan);
            }
        });

        return result;
    }

    public List<Loan> getAllLoansForUser(String userId) throws Exception {
        ValidationUtil.validateNotEmpty(userId, "ID del usuario");

        userService.getUserById(userId);

        List<Loan> result = new ArrayList<>();
        loans.forEach(loan -> {
            if (loan.getUser().getId().equals(userId)) {
                result.add(loan);
            }
        });

        return result;
    }

    public List<Loan> getLoansForBook(String bookId) {
        ValidationUtil.validateNotEmpty(bookId, "ID del libro");

        bookService.getBookById(bookId);

        List<Loan> result = new ArrayList<>();
        loans.forEach(loan -> {
            if (loan.getBook().getId().equals(bookId)) {
                result.add(loan);
            }
        });

        return result;
    }

    public List<Loan> getOverdueLoans() {
        List<Loan> result = new ArrayList<>();

        loans.forEach(loan -> {
            if (loan.isStatus() && DateUtil.isOverdue(loan.getReturnDate())) {
                result.add(loan);
            }
        });

        return result;
    }

    public List<Loan> getOverdueLoansForUser(String userId) throws Exception {
        ValidationUtil.validateNotEmpty(userId, "ID del usuario");

        userService.getUserById(userId);

        List<Loan> result = new ArrayList<>();

        loans.forEach(loan -> {
            if (loan.getUser().getId().equals(userId) &&
                    loan.isStatus() &&
                    DateUtil.isOverdue(loan.getReturnDate())) {
                result.add(loan);
            }
        });

        return result;
    }

    public long calculateFine(String loanId) {
        Loan loan = getLoanById(loanId);
        long overdueDays = DateUtil.getOverdueDays(loan.getReturnDate());
        return overdueDays * 1000;
    }

    public int getTotalActiveLoans() {
        return (int) loans.stream().filter(Loan::isStatus).count();
    }

    public int getTotalCompletedLoans() {
        return (int) loans.stream().filter(loan -> !loan.isStatus()).count();
    }

    public Loan renewLoan(String loanId, int additionalDays) {
        Loan loan = getLoanById(loanId);

        if (!loan.isStatus()) {
            throw new IllegalArgumentException("No se puede renovar un préstamo que ya ha sido devuelto");
        }

        if (additionalDays <= 0) {
            throw new IllegalArgumentException("El número de días adicionales debe ser positivo");
        }

        Date newReturnDate = DateUtil.addDays(loan.getReturnDate(), additionalDays);
        loan.setReturnDate(newReturnDate);

        return loan;
    }
}
