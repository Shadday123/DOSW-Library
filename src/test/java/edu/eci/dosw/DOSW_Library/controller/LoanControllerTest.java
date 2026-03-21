package edu.eci.dosw.DOSW_Library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.ILoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
@DisplayName("Pruebas unitarias de LoanController")
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ILoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    private Loan testLoan;
    private Book testBook;
    private User testUser;
    private Date loanDate;
    private Date returnDate;

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

        testLoan = new Loan();
        testLoan.setId("LN_test01");
        testLoan.setBook(testBook);
        testLoan.setUser(testUser);
        testLoan.setLoanDate(loanDate);
        testLoan.setReturnDate(returnDate);
        testLoan.setStatus(true);
        testLoan.setActualReturnDate(null);
    }

    // --- POST /api/loans ---

    @Test
    @DisplayName("POST /api/loans - Crea préstamo exitosamente y retorna 201")
    void createLoan_success_returns201() throws Exception {
        when(loanService.createLoan("USR_test01", "BK_test01")).thenReturn(testLoan);

        mockMvc.perform(post("/api/loans")
                        .param("userId", "USR_test01")
                        .param("bookId", "BK_test01"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("LN_test01"))
                .andExpect(jsonPath("$.userId").value("USR_test01"))
                .andExpect(jsonPath("$.bookId").value("BK_test01"))
                .andExpect(jsonPath("$.status").value(true));

        verify(loanService).createLoan("USR_test01", "BK_test01");
    }

    @Test
    @DisplayName("POST /api/loans - Usuario no encontrado propaga excepción")
    void createLoan_userNotFound_propagatesException() {
        when(loanService.createLoan("USR_inexistente", "BK_test01"))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/loans")
                        .param("userId", "USR_inexistente")
                        .param("bookId", "BK_test01")));
    }

    @Test
    @DisplayName("POST /api/loans - Libro no disponible propaga excepción")
    void createLoan_bookNotAvailable_propagatesException() {
        when(loanService.createLoan("USR_test01", "BK_test01"))
                .thenThrow(new BookNotAvailableException("No hay copias disponibles"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/loans")
                        .param("userId", "USR_test01")
                        .param("bookId", "BK_test01")));
    }

    @Test
    @DisplayName("POST /api/loans - Límite de préstamos excedido propaga excepción")
    void createLoan_loanLimitExceeded_propagatesException() {
        when(loanService.createLoan("USR_test01", "BK_test01"))
                .thenThrow(new LoanLimitExceededException("Límite de 5 préstamos alcanzado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/loans")
                        .param("userId", "USR_test01")
                        .param("bookId", "BK_test01")));
    }

    // --- PUT /api/loans/{loanId}/return ---

    @Test
    @DisplayName("PUT /api/loans/{loanId}/return - Devuelve libro exitosamente con 200")
    void returnLoan_success_returns200() throws Exception {
        testLoan.setStatus(false);
        testLoan.setActualReturnDate(new Date());
        when(loanService.returnLoan("LN_test01")).thenReturn(testLoan);

        mockMvc.perform(put("/api/loans/LN_test01/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("LN_test01"))
                .andExpect(jsonPath("$.status").value(false));

        verify(loanService).returnLoan("LN_test01");
    }

    @Test
    @DisplayName("PUT /api/loans/{loanId}/return - Préstamo ya devuelto propaga excepción")
    void returnLoan_alreadyReturned_propagatesException() {
        when(loanService.returnLoan("LN_test01"))
                .thenThrow(new IllegalStateException("El préstamo ya fue devuelto"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(put("/api/loans/LN_test01/return")));
    }

    @Test
    @DisplayName("PUT /api/loans/{loanId}/return - Préstamo no encontrado propaga excepción")
    void returnLoan_notFound_propagatesException() {
        when(loanService.returnLoan("LN_inexistente"))
                .thenThrow(new IllegalArgumentException("Préstamo no encontrado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(put("/api/loans/LN_inexistente/return")));
    }

    // --- GET /api/loans/{loanId} ---

    @Test
    @DisplayName("GET /api/loans/{loanId} - Retorna préstamo por ID con 200")
    void getLoanById_found_returns200() throws Exception {
        when(loanService.getLoanById("LN_test01")).thenReturn(testLoan);

        mockMvc.perform(get("/api/loans/LN_test01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("LN_test01"))
                .andExpect(jsonPath("$.userId").value("USR_test01"))
                .andExpect(jsonPath("$.bookId").value("BK_test01"));
    }

    @Test
    @DisplayName("GET /api/loans/{loanId} - Préstamo no encontrado propaga excepción")
    void getLoanById_notFound_propagatesException() {
        when(loanService.getLoanById("LN_inexistente"))
                .thenThrow(new IllegalArgumentException("Préstamo no encontrado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/loans/LN_inexistente")));
    }

    // --- GET /api/loans ---

    @Test
    @DisplayName("GET /api/loans - Retorna todos los préstamos con 200")
    void getAllLoans_returnsList() throws Exception {
        when(loanService.getAllLoans()).thenReturn(Arrays.asList(testLoan));

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("LN_test01"));
    }

    @Test
    @DisplayName("GET /api/loans - Sin préstamos retorna lista vacía")
    void getAllLoans_emptyList_returnsEmpty() throws Exception {
        when(loanService.getAllLoans()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/loans/user/{userId}/active ---

    @Test
    @DisplayName("GET /api/loans/user/{userId}/active - Retorna préstamos activos del usuario")
    void getActiveLoansForUser_returnsList() throws Exception {
        when(loanService.getActiveLoansForUser("USR_test01")).thenReturn(Arrays.asList(testLoan));

        mockMvc.perform(get("/api/loans/user/USR_test01/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(true));
    }

    @Test
    @DisplayName("GET /api/loans/user/{userId}/active - Usuario sin préstamos activos")
    void getActiveLoansForUser_noActiveLoans_returnsEmpty() throws Exception {
        when(loanService.getActiveLoansForUser("USR_test01")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/loans/user/USR_test01/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/loans/user/{userId} ---

    @Test
    @DisplayName("GET /api/loans/user/{userId} - Retorna historial completo del usuario")
    void getAllLoansForUser_returnsList() throws Exception {
        Loan returnedLoan = new Loan();
        returnedLoan.setId("LN_test02");
        returnedLoan.setBook(testBook);
        returnedLoan.setUser(testUser);
        returnedLoan.setLoanDate(loanDate);
        returnedLoan.setReturnDate(returnDate);
        returnedLoan.setStatus(false);
        returnedLoan.setActualReturnDate(new Date());

        when(loanService.getAllLoansForUser("USR_test01"))
                .thenReturn(Arrays.asList(testLoan, returnedLoan));

        mockMvc.perform(get("/api/loans/user/USR_test01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // --- GET /api/loans/book/{bookId} ---

    @Test
    @DisplayName("GET /api/loans/book/{bookId} - Retorna préstamos del libro")
    void getLoansForBook_returnsList() throws Exception {
        when(loanService.getLoansForBook("BK_test01")).thenReturn(Arrays.asList(testLoan));

        mockMvc.perform(get("/api/loans/book/BK_test01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].bookId").value("BK_test01"));
    }

    // --- GET /api/loans/overdue ---

    @Test
    @DisplayName("GET /api/loans/overdue - Retorna préstamos vencidos")
    void getOverdueLoans_returnsList() throws Exception {
        Loan overdueLoan = new Loan();
        overdueLoan.setId("LN_overdue01");
        overdueLoan.setBook(testBook);
        overdueLoan.setUser(testUser);
        overdueLoan.setLoanDate(new Date(System.currentTimeMillis() - 20L * 24 * 60 * 60 * 1000));
        overdueLoan.setReturnDate(new Date(System.currentTimeMillis() - 6L * 24 * 60 * 60 * 1000));
        overdueLoan.setStatus(true);

        when(loanService.getOverdueLoans()).thenReturn(Arrays.asList(overdueLoan));

        mockMvc.perform(get("/api/loans/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("LN_overdue01"));
    }

    @Test
    @DisplayName("GET /api/loans/overdue - Sin préstamos vencidos retorna lista vacía")
    void getOverdueLoans_none_returnsEmpty() throws Exception {
        when(loanService.getOverdueLoans()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/loans/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/loans/user/{userId}/overdue ---

    @Test
    @DisplayName("GET /api/loans/user/{userId}/overdue - Retorna préstamos vencidos del usuario")
    void getOverdueLoansForUser_returnsList() throws Exception {
        when(loanService.getOverdueLoansForUser("USR_test01")).thenReturn(Arrays.asList(testLoan));

        mockMvc.perform(get("/api/loans/user/USR_test01/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // --- GET /api/loans/{loanId}/fine ---

    @Test
    @DisplayName("GET /api/loans/{loanId}/fine - Retorna multa calculada")
    void calculateFine_overdueLoan_returnsFine() throws Exception {
        when(loanService.calculateFine("LN_test01")).thenReturn(5000L);

        mockMvc.perform(get("/api/loans/LN_test01/fine"))
                .andExpect(status().isOk())
                .andExpect(content().string("5000"));
    }

    @Test
    @DisplayName("GET /api/loans/{loanId}/fine - Préstamo al día retorna multa cero")
    void calculateFine_notOverdue_returnsZero() throws Exception {
        when(loanService.calculateFine("LN_test01")).thenReturn(0L);

        mockMvc.perform(get("/api/loans/LN_test01/fine"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    // --- PUT /api/loans/{loanId}/renew ---

    @Test
    @DisplayName("PUT /api/loans/{loanId}/renew - Renueva préstamo exitosamente con 200")
    void renewLoan_success_returns200() throws Exception {
        Date newReturnDate = new Date(returnDate.getTime() + 7L * 24 * 60 * 60 * 1000);
        testLoan.setReturnDate(newReturnDate);
        when(loanService.renewLoan("LN_test01", 7)).thenReturn(testLoan);

        mockMvc.perform(put("/api/loans/LN_test01/renew")
                        .param("additionalDays", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("LN_test01"));

        verify(loanService).renewLoan("LN_test01", 7);
    }

    @Test
    @DisplayName("PUT /api/loans/{loanId}/renew - Préstamo ya devuelto propaga excepción")
    void renewLoan_alreadyReturned_propagatesException() {
        when(loanService.renewLoan("LN_test01", 7))
                .thenThrow(new IllegalStateException("No se puede renovar un préstamo devuelto"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(put("/api/loans/LN_test01/renew")
                        .param("additionalDays", "7")));
    }

    // --- GET /api/loans/stats/active ---

    @Test
    @DisplayName("GET /api/loans/stats/active - Retorna total de préstamos activos")
    void getTotalActiveLoans_returnsCount() throws Exception {
        when(loanService.getTotalActiveLoans()).thenReturn(3);

        mockMvc.perform(get("/api/loans/stats/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @DisplayName("GET /api/loans/stats/active - Sin préstamos activos retorna 0")
    void getTotalActiveLoans_none_returnsZero() throws Exception {
        when(loanService.getTotalActiveLoans()).thenReturn(0);

        mockMvc.perform(get("/api/loans/stats/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    // --- GET /api/loans/stats/completed ---

    @Test
    @DisplayName("GET /api/loans/stats/completed - Retorna total de préstamos completados")
    void getTotalCompletedLoans_returnsCount() throws Exception {
        when(loanService.getTotalCompletedLoans()).thenReturn(7);

        mockMvc.perform(get("/api/loans/stats/completed"))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }

    @Test
    @DisplayName("GET /api/loans/stats/completed - Sin préstamos completados retorna 0")
    void getTotalCompletedLoans_none_returnsZero() throws Exception {
        when(loanService.getTotalCompletedLoans()).thenReturn(0);

        mockMvc.perform(get("/api/loans/stats/completed"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
