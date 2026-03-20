package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import edu.eci.dosw.DOSW_Library.core.service.LoanService;
import edu.eci.dosw.DOSW_Library.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de LoanService")
class LoanServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private LoanService loanService;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("USR_test01");
        testUser.setName("Juan Pérez");

        testBook = new Book();
        testBook.setId("BK_test01");
        testBook.setTitle("El Quijote");
        testBook.setAuthor("Cervantes");
        testBook.setTotalCopies(3);
        testBook.setAvailableCopies(3);
    }

    // ==================== createLoan ====================

    @Test
    @DisplayName("createLoan: debe crear un préstamo exitosamente")
    void createLoan_success() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");

        assertNotNull(loan);
        assertNotNull(loan.getId());
        assertTrue(loan.getId().startsWith("LN_"));
        assertEquals(testUser, loan.getUser());
        assertEquals(testBook, loan.getBook());
        assertTrue(loan.isStatus());
        assertNotNull(loan.getLoanDate());
        assertNotNull(loan.getReturnDate());
        assertTrue(loan.getReturnDate().after(loan.getLoanDate()));
        verify(bookService).decreaseAvailableCopies("BK_test01");
    }

    @Test
    @DisplayName("createLoan: debe lanzar UserNotFoundException si el usuario no existe")
    void createLoan_userNotFound_throwsUserNotFoundException() {
        when(userService.getUserById("USR_inexistente"))
                .thenThrow(new UserNotFoundException("USR_inexistente", true));

        assertThrows(UserNotFoundException.class,
                () -> loanService.createLoan("USR_inexistente", "BK_test01"));

        verify(bookService, never()).decreaseAvailableCopies(anyString());
    }

    @Test
    @DisplayName("createLoan: debe lanzar BookNotAvailableException si no hay copias")
    void createLoan_bookNotAvailable_throwsBookNotAvailableException() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(false);

        assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan("USR_test01", "BK_test01"));

        verify(bookService, never()).decreaseAvailableCopies(anyString());
    }

    @Test
    @DisplayName("createLoan: debe lanzar LoanLimitExceededException al superar el límite")
    void createLoan_exceedsMaxLoans_throwsLoanLimitExceededException() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById(anyString())).thenReturn(testBook);
        when(bookService.isBookAvailable(anyString())).thenReturn(true);

        // Crear el máximo de préstamos permitidos
        for (int i = 0; i < LoanService.MAX_ACTIVE_LOANS; i++) {
            Book extraBook = new Book();
            extraBook.setId("BK_extra0" + i);
            extraBook.setTitle("Libro " + i);
            extraBook.setAuthor("Autor");
            extraBook.setTotalCopies(5);
            extraBook.setAvailableCopies(5);
            when(bookService.getBookById("BK_extra0" + i)).thenReturn(extraBook);
            when(bookService.isBookAvailable("BK_extra0" + i)).thenReturn(true);
            loanService.createLoan("USR_test01", "BK_extra0" + i);
        }

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.createLoan("USR_test01", "BK_test01"));
    }

    @Test
    @DisplayName("createLoan: debe lanzar excepción si el userId está vacío")
    void createLoan_emptyUserId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan("", "BK_test01"));
    }

    @Test
    @DisplayName("createLoan: debe lanzar excepción si el bookId está vacío")
    void createLoan_emptyBookId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan("USR_test01", ""));
    }

    // ==================== returnLoan ====================

    @Test
    @DisplayName("returnLoan: debe registrar la devolución correctamente")
    void returnLoan_success() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");
        Loan returned = loanService.returnLoan(loan.getId());

        assertFalse(returned.isStatus());
        assertNotNull(returned.getActualReturnDate());
        verify(bookService).increaseAvailableCopies("BK_test01");
    }

    @Test
    @DisplayName("returnLoan: debe lanzar excepción si el préstamo ya fue devuelto")
    void returnLoan_alreadyReturned_throwsException() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");
        loanService.returnLoan(loan.getId());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.returnLoan(loan.getId())
        );
        assertTrue(ex.getMessage().contains("ya ha sido devuelto"));
    }

    @Test
    @DisplayName("returnLoan: debe lanzar excepción si el préstamo no existe")
    void returnLoan_notFound_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnLoan("LN_inexistente"));
    }

    // ==================== getLoanById ====================

    @Test
    @DisplayName("getLoanById: debe retornar el préstamo correcto")
    void getLoanById_exists_returnsLoan() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan created = loanService.createLoan("USR_test01", "BK_test01");
        Loan found = loanService.getLoanById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("getLoanById: debe lanzar excepción si el préstamo no existe")
    void getLoanById_notFound_throwsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.getLoanById("LN_inexistente")
        );
        assertTrue(ex.getMessage().contains("LN_inexistente"));
    }

    @Test
    @DisplayName("getLoanById: debe lanzar excepción si el ID es vacío")
    void getLoanById_emptyId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.getLoanById(""));
    }

    // ==================== getAllLoans ====================

    @Test
    @DisplayName("getAllLoans: debe retornar lista vacía si no hay préstamos")
    void getAllLoans_empty_returnsEmptyList() {
        List<Loan> loans = loanService.getAllLoans();
        assertNotNull(loans);
        assertTrue(loans.isEmpty());
    }

    @Test
    @DisplayName("getAllLoans: debe retornar todos los préstamos")
    void getAllLoans_withLoans_returnsAll() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        loanService.createLoan("USR_test01", "BK_test01");

        assertEquals(1, loanService.getAllLoans().size());
    }

    // ==================== getActiveLoansForUser ====================

    @Test
    @DisplayName("getActiveLoansForUser: debe retornar solo préstamos activos del usuario")
    void getActiveLoansForUser_returnsOnlyActive() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");
        loanService.returnLoan(loan.getId());

        Book book2 = new Book();
        book2.setId("BK_test02");
        book2.setTitle("Libro 2");
        book2.setAuthor("Autor 2");
        book2.setTotalCopies(2);
        book2.setAvailableCopies(2);
        when(bookService.getBookById("BK_test02")).thenReturn(book2);
        when(bookService.isBookAvailable("BK_test02")).thenReturn(true);
        loanService.createLoan("USR_test01", "BK_test02");

        List<Loan> activeLoans = loanService.getActiveLoansForUser("USR_test01");
        assertEquals(1, activeLoans.size());
        assertTrue(activeLoans.get(0).isStatus());
    }

    @Test
    @DisplayName("getActiveLoansForUser: debe lanzar UserNotFoundException si el usuario no existe")
    void getActiveLoansForUser_userNotFound_throwsException() {
        when(userService.getUserById("USR_inexistente"))
                .thenThrow(new UserNotFoundException("USR_inexistente", true));

        assertThrows(UserNotFoundException.class,
                () -> loanService.getActiveLoansForUser("USR_inexistente"));
    }

    // ==================== getAllLoansForUser ====================

    @Test
    @DisplayName("getAllLoansForUser: debe retornar todos los préstamos del usuario")
    void getAllLoansForUser_returnsAllLoans() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");
        loanService.returnLoan(loan.getId());

        Book book2 = new Book();
        book2.setId("BK_test02");
        book2.setTitle("Libro 2");
        book2.setAuthor("Autor 2");
        book2.setTotalCopies(2);
        book2.setAvailableCopies(2);
        when(bookService.getBookById("BK_test02")).thenReturn(book2);
        when(bookService.isBookAvailable("BK_test02")).thenReturn(true);
        loanService.createLoan("USR_test01", "BK_test02");

        List<Loan> allLoans = loanService.getAllLoansForUser("USR_test01");
        assertEquals(2, allLoans.size());
    }

    // ==================== getLoansForBook ====================

    @Test
    @DisplayName("getLoansForBook: debe retornar préstamos del libro correcto")
    void getLoansForBook_returnsLoansForBook() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        loanService.createLoan("USR_test01", "BK_test01");

        List<Loan> loans = loanService.getLoansForBook("BK_test01");
        assertEquals(1, loans.size());
        assertEquals("BK_test01", loans.get(0).getBook().getId());
    }

    // ==================== getOverdueLoans ====================

    @Test
    @DisplayName("getOverdueLoans: debe retornar préstamos vencidos")
    void getOverdueLoans_returnsOverdueLoans() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");

        // Simular que la fecha de devolución ya pasó
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        loan.setReturnDate(cal.getTime());

        List<Loan> overdue = loanService.getOverdueLoans();
        assertEquals(1, overdue.size());
    }

    @Test
    @DisplayName("getOverdueLoans: no debe incluir préstamos devueltos aunque estén vencidos")
    void getOverdueLoans_excludesReturnedLoans() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        loan.setReturnDate(cal.getTime());
        loanService.returnLoan(loan.getId());

        List<Loan> overdue = loanService.getOverdueLoans();
        assertTrue(overdue.isEmpty());
    }

    // ==================== calculateFine ====================

    @Test
    @DisplayName("calculateFine: debe retornar 0 si el préstamo no está vencido")
    void calculateFine_notOverdue_returnsZero() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");
        long fine = loanService.calculateFine(loan.getId());

        assertEquals(0, fine);
    }

    @Test
    @DisplayName("calculateFine: debe calcular la multa correctamente (1000 pesos/día)")
    void calculateFine_overdue_calculatesCorrectly() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");

        // Simular 5 días de atraso
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        loan.setReturnDate(cal.getTime());

        long fine = loanService.calculateFine(loan.getId());
        assertTrue(fine >= 5000); // Al menos 5 días × 1000
    }

    // ==================== renewLoan ====================

    @Test
    @DisplayName("renewLoan: debe extender la fecha de devolución")
    void renewLoan_success() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");
        Date originalReturnDate = loan.getReturnDate();

        Loan renewed = loanService.renewLoan(loan.getId(), 7);

        assertTrue(renewed.getReturnDate().after(originalReturnDate));
    }

    @Test
    @DisplayName("renewLoan: debe lanzar excepción si el préstamo ya fue devuelto")
    void renewLoan_alreadyReturned_throwsException() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");
        loanService.returnLoan(loan.getId());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.renewLoan(loan.getId(), 7)
        );
        assertTrue(ex.getMessage().contains("ya ha sido devuelto"));
    }

    @Test
    @DisplayName("renewLoan: debe lanzar excepción si los días adicionales son 0 o negativos")
    void renewLoan_negativeDays_throwsException() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");

        assertThrows(IllegalArgumentException.class,
                () -> loanService.renewLoan(loan.getId(), 0));

        assertThrows(IllegalArgumentException.class,
                () -> loanService.renewLoan(loan.getId(), -3));
    }

    @Test
    @DisplayName("renewLoan: debe lanzar excepción si el préstamo no existe")
    void renewLoan_notFound_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.renewLoan("LN_inexistente", 7));
    }

    // ==================== getTotalActiveLoans & getTotalCompletedLoans ====================

    @Test
    @DisplayName("getTotalActiveLoans: debe contar correctamente los préstamos activos")
    void getTotalActiveLoans_returnsCorrectCount() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        assertEquals(0, loanService.getTotalActiveLoans());

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");
        assertEquals(1, loanService.getTotalActiveLoans());

        loanService.returnLoan(loan.getId());
        assertEquals(0, loanService.getTotalActiveLoans());
    }

    @Test
    @DisplayName("getTotalCompletedLoans: debe contar correctamente los préstamos completados")
    void getTotalCompletedLoans_returnsCorrectCount() {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        assertEquals(0, loanService.getTotalCompletedLoans());

        Loan loan = loanService.createLoan("USR_test01", "BK_test01");
        assertEquals(0, loanService.getTotalCompletedLoans());

        loanService.returnLoan(loan.getId());
        assertEquals(1, loanService.getTotalCompletedLoans());
    }
}