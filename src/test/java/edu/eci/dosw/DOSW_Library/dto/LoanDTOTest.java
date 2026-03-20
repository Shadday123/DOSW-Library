package edu.eci.dosw.DOSW_Library.dto;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de LoanDTO")
class LoanDTOTest {

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
    @DisplayName("modelToDTO - Convierte Loan activo a LoanDTO correctamente")
    void modelToDTO_activeLoan_returnsCorrectDTO() {
        LoanDTO result = LoanDTO.modelToDTO(testLoan);

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
    @DisplayName("modelToDTO - Préstamo nulo retorna null")
    void modelToDTO_nullLoan_returnsNull() {
        LoanDTO result = LoanDTO.modelToDTO(null);

        assertNull(result);
    }

    @Test
    @DisplayName("modelToDTO - Préstamo devuelto incluye fecha de devolución real")
    void modelToDTO_returnedLoan_includesActualReturnDate() {
        testLoan.setStatus(false);
        testLoan.setActualReturnDate(actualReturnDate);

        LoanDTO result = LoanDTO.modelToDTO(testLoan);

        assertNotNull(result);
        assertFalse(result.isStatus());
        assertEquals(actualReturnDate, result.getActualReturnDate());
    }

    @Test
    @DisplayName("modelToDTO - Extrae ID del libro correctamente")
    void modelToDTO_extractsBookId() {
        LoanDTO result = LoanDTO.modelToDTO(testLoan);

        assertEquals("BK_test01", result.getBookId());
    }

    @Test
    @DisplayName("modelToDTO - Extrae ID del usuario correctamente")
    void modelToDTO_extractsUserId() {
        LoanDTO result = LoanDTO.modelToDTO(testLoan);

        assertEquals("USR_test01", result.getUserId());
    }

    @Test
    @DisplayName("modelToDTO - Status false indica préstamo devuelto")
    void modelToDTO_returnedStatus_isFalse() {
        testLoan.setStatus(false);

        LoanDTO result = LoanDTO.modelToDTO(testLoan);

        assertFalse(result.isStatus());
    }

    @Test
    @DisplayName("modelToDTO - Preserva fechas de préstamo y devolución esperada")
    void modelToDTO_preservesDates() {
        LoanDTO result = LoanDTO.modelToDTO(testLoan);

        assertEquals(loanDate, result.getLoanDate());
        assertEquals(returnDate, result.getReturnDate());
    }

    // --- Constructor y getters/setters (Lombok @Data) ---

    @Test
    @DisplayName("Constructor sin argumentos crea objeto vacío")
    void noArgsConstructor_createsEmptyObject() {
        LoanDTO dto = new LoanDTO();

        assertNull(dto.getId());
        assertNull(dto.getBookId());
        assertNull(dto.getUserId());
        assertNull(dto.getLoanDate());
        assertNull(dto.getReturnDate());
        assertNull(dto.getActualReturnDate());
        assertFalse(dto.isStatus());
    }

    @Test
    @DisplayName("Constructor con todos los argumentos inicializa correctamente")
    void allArgsConstructor_initializesCorrectly() {
        LoanDTO dto = new LoanDTO("LN_001", "BK_001", "USR_001", loanDate, returnDate, null, true);

        assertEquals("LN_001", dto.getId());
        assertEquals("BK_001", dto.getBookId());
        assertEquals("USR_001", dto.getUserId());
        assertEquals(loanDate, dto.getLoanDate());
        assertEquals(returnDate, dto.getReturnDate());
        assertNull(dto.getActualReturnDate());
        assertTrue(dto.isStatus());
    }

    @Test
    @DisplayName("Setters actualizan los campos correctamente")
    void setters_updateFieldsCorrectly() {
        LoanDTO dto = new LoanDTO();
        dto.setId("LN_nuevo");
        dto.setBookId("BK_nuevo");
        dto.setUserId("USR_nuevo");
        dto.setLoanDate(loanDate);
        dto.setReturnDate(returnDate);
        dto.setStatus(true);

        assertEquals("LN_nuevo", dto.getId());
        assertEquals("BK_nuevo", dto.getBookId());
        assertEquals("USR_nuevo", dto.getUserId());
        assertEquals(loanDate, dto.getLoanDate());
        assertEquals(returnDate, dto.getReturnDate());
        assertTrue(dto.isStatus());
    }
}
