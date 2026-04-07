package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.util.DateUtil;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import edu.eci.dosw.DOSW_Library.persistence.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanService implements ILoanService {

    private static final int LOAN_DURATION_DAYS = 14;
    public static final int MAX_ACTIVE_LOANS = 5;

    private final LoanRepository loanRepository;
    private final IUserService userService;
    private final IBookService bookService;

    public LoanService(LoanRepository loanRepository, IUserService userService, IBookService bookService) {
        this.loanRepository = loanRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    public Loan createLoan(String userId, String bookId) {
        ValidationUtil.validateNotEmpty(userId, "ID del usuario");
        ValidationUtil.validateNotEmpty(bookId, "ID del libro");

        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        long activeLoansCount = loanRepository.findActiveByUserId(userId).size();
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
        return loanRepository.save(loan);
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
        return loanRepository.save(loan);
    }

    public Loan getLoanById(String loanId) {
        ValidationUtil.validateNotEmpty(loanId, "ID del préstamo");

        return loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado con ID: " + loanId));
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}
