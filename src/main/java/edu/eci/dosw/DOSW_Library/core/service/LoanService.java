package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.util.DateUtil;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService implements ILoanService {

    private static final int LOAN_DURATION_DAYS = 14;
    public static final int MAX_ACTIVE_LOANS = 5;

    private List<Loan> loans;
    private IUserService userService;
    private IBookService bookService;

    public LoanService() {
        this.loans = new ArrayList<>();
    }

    @Autowired
    @Required
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @Autowired
    @Required
    public void setBookService(IBookService bookService) {
        this.bookService = bookService;
    }

    public Loan createLoan(String userId, String bookId) {
        ValidationUtil.validateNotEmpty(userId, "ID del usuario");
        ValidationUtil.validateNotEmpty(bookId, "ID del libro");

        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        long activeLoansCount = loans.stream()
                .filter(l -> l.getUser().getId().equals(userId) && l.isStatus())
                .count();
        if (activeLoansCount >= MAX_ACTIVE_LOANS) {
            throw new LoanLimitExceededException(userId, MAX_ACTIVE_LOANS);
        }

        if (!bookService.isBookAvailable(bookId)) {
            throw new BookNotAvailableException(bookId, book.getTitle());
        }

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
            throw new IllegalArgumentException("El préstamo con ID '" + loanId + "' ya ha sido devuelto");
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
}
