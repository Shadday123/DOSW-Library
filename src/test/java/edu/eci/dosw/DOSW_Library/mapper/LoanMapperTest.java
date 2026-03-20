package edu.eci.dosw.DOSW_Library.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.controller.mapper.LoanMapper;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de LoanMapper")
class LoanMapperTest {

    private Loan testLoan;
    private Book testBook;
    private User testUser;
    private Date loanDate;
    private Date returnDate;
    private Date actualReturnDate;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId("BK_test01");
        testBook.setTitle("El Quijote");
        testBook.setAuthor("Cervantes");
        testBook.setAvailableCopies(3);
        testBook.setTotalCopies(5);

        testUser = new User();
        testUser.setId("USR_test01");
        testUser.setName("Juan Pérez");

        loanDate = new Date();
        returnDate = new Date(loanDate.getTime() + 14L * 24 * 60 * 60 * 1000);
        actualReturnDate = new Date();

        testLoan = new Loan();
        testLoan.setId("LN_test01");
        testLoan.setBook(testBook);
        testLoan.setUser(testUser);
        testLoan.setLoanDate(loanDate);
        testLoan.setReturnDate(returnDate);
        testLoan.setStatus(true);
        testLoan.setActualReturnDate(null);
    }

    // --- modelToDTO ---

    @Test
    @DisplayName("modelToDTO - Convierte Loan activo a LoanDTO con todos los campos")
    void modelToDTO_activeLoan_mapsAllFields() {
        LoanDTO result = LoanMapper.modelToDTO(testLoan);

        assertNotNull(result);
        assertEquals("LN_test01", result.getId());
        assertEquals("BK_test01", result.getBookId());
        assertEquals("USR_test01", result.getUserId());
        assertEquals(loanDate, result.getLoanDate());
        assertEquals(returnDate, result.getReturnDate());
        assertTrue(result.isStatus());
        assertNull(result.getActualReturnDate());
    }

    @Test
    @DisplayName("modelToDTO - Loan nulo retorna null")
    void modelToDTO_nullLoan_returnsNull() {
        LoanDTO result = LoanMapper.modelToDTO(null);

        assertNull(result);
    }

    @Test
    @DisplayName("modelToDTO - Préstamo devuelto incluye fecha real de devolución")
    void modelToDTO_returnedLoan_includesActualReturnDate() {
        testLoan.setStatus(false);
        testLoan.setActualReturnDate(actualReturnDate);

        LoanDTO result = LoanMapper.modelToDTO(testLoan);

        assertNotNull(result);
        assertFalse(result.isStatus());
        assertEquals(actualReturnDate, result.getActualReturnDate());
    }

    @Test
    @DisplayName("modelToDTO - Libro null en préstamo mapea bookId como null (null-safe)")
    void modelToDTO_nullBook_mapsBookIdAsNull() {
        testLoan.setBook(null);

        LoanDTO result = LoanMapper.modelToDTO(testLoan);

        assertNotNull(result);
        assertNull(result.getBookId());
    }

    @Test
    @DisplayName("modelToDTO - Usuario null en préstamo mapea userId como null (null-safe)")
    void modelToDTO_nullUser_mapsUserIdAsNull() {
        testLoan.setUser(null);

        LoanDTO result = LoanMapper.modelToDTO(testLoan);

        assertNotNull(result);
        assertNull(result.getUserId());
    }

    @Test
    @DisplayName("modelToDTO - Libro y usuario null se manejan de forma segura")
    void modelToDTO_nullBookAndUser_handlesSafely() {
        testLoan.setBook(null);
        testLoan.setUser(null);

        LoanDTO result = LoanMapper.modelToDTO(testLoan);

        assertNotNull(result);
        assertNull(result.getBookId());
        assertNull(result.getUserId());
        assertEquals("LN_test01", result.getId());
    }

    @Test
    @DisplayName("modelToDTO - Preserva las fechas de préstamo y devolución esperada")
    void modelToDTO_preservesDates() {
        LoanDTO result = LoanMapper.modelToDTO(testLoan);

        assertEquals(loanDate, result.getLoanDate());
        assertEquals(returnDate, result.getReturnDate());
    }

    @Test
    @DisplayName("modelToDTO - Status true indica préstamo activo")
    void modelToDTO_activeStatus_isTrue() {
        testLoan.setStatus(true);

        LoanDTO result = LoanMapper.modelToDTO(testLoan);

        assertTrue(result.isStatus());
    }

    @Test
    @DisplayName("modelToDTO - Status false indica préstamo devuelto")
    void modelToDTO_returnedStatus_isFalse() {
        testLoan.setStatus(false);

        LoanDTO result = LoanMapper.modelToDTO(testLoan);

        assertFalse(result.isStatus());
    }

    @Test
    @DisplayName("modelToDTO - Crea una nueva instancia de LoanDTO")
    void modelToDTO_returnsNewInstance() {
        LoanDTO result1 = LoanMapper.modelToDTO(testLoan);
        LoanDTO result2 = LoanMapper.modelToDTO(testLoan);

        assertNotSame(result1, result2);
    }

    @Test
    @DisplayName("modelToDTO - ID del préstamo con prefijo LN_ se mapea correctamente")
    void modelToDTO_loanIdWithPrefix_mapsCorrectly() {
        testLoan.setId("LN_abc123def456");

        LoanDTO result = LoanMapper.modelToDTO(testLoan);

        assertEquals("LN_abc123def456", result.getId());
    }

    @Test
    @DisplayName("modelToDTO - Fechas nulas se mapean como null")
    void modelToDTO_nullDates_mapsAsNull() {
        testLoan.setLoanDate(null);
        testLoan.setReturnDate(null);

        LoanDTO result = LoanMapper.modelToDTO(testLoan);

        assertNull(result.getLoanDate());
        assertNull(result.getReturnDate());
    }
}
