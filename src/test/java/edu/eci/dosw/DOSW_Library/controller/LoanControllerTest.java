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
    @DisplayName("POST /api/loans - Usuario no encontrado retorna 404")
    void createLoan_userNotFound_returns404() throws Exception {
        when(loanService.createLoan("USR_inexistente", "BK_test01"))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));

        mockMvc.perform(post("/api/loans")
                        .param("userId", "USR_inexistente")
                        .param("bookId", "BK_test01"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
    }

    @Test
    @DisplayName("POST /api/loans - Libro no disponible retorna 409")
    void createLoan_bookNotAvailable_returns409() throws Exception {
        when(loanService.createLoan("USR_test01", "BK_test01"))
                .thenThrow(new BookNotAvailableException("No hay copias disponibles"));

        mockMvc.perform(post("/api/loans")
                        .param("userId", "USR_test01")
                        .param("bookId", "BK_test01"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("No hay copias disponibles"));
    }

    @Test
    @DisplayName("POST /api/loans - Límite de préstamos excedido retorna 400")
    void createLoan_loanLimitExceeded_returns400() throws Exception {
        when(loanService.createLoan("USR_test01", "BK_test01"))
                .thenThrow(new LoanLimitExceededException("Límite de 5 préstamos alcanzado"));

        mockMvc.perform(post("/api/loans")
                        .param("userId", "USR_test01")
                        .param("bookId", "BK_test01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Límite de 5 préstamos alcanzado"));
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
    @DisplayName("PUT /api/loans/{loanId}/return - Préstamo ya devuelto retorna 500")
    void returnLoan_alreadyReturned_returns500() throws Exception {
        when(loanService.returnLoan("LN_test01"))
                .thenThrow(new IllegalStateException("El préstamo ya fue devuelto"));

        mockMvc.perform(put("/api/loans/LN_test01/return"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /api/loans/{loanId}/return - Préstamo no encontrado retorna 400")
    void returnLoan_notFound_returns400() throws Exception {
        when(loanService.returnLoan("LN_inexistente"))
                .thenThrow(new IllegalArgumentException("Préstamo no encontrado"));

        mockMvc.perform(put("/api/loans/LN_inexistente/return"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Préstamo no encontrado"));
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
    @DisplayName("GET /api/loans/{loanId} - Préstamo no encontrado retorna 400")
    void getLoanById_notFound_returns400() throws Exception {
        when(loanService.getLoanById("LN_inexistente"))
                .thenThrow(new IllegalArgumentException("Préstamo no encontrado"));

        mockMvc.perform(get("/api/loans/LN_inexistente"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Préstamo no encontrado"));
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
}
