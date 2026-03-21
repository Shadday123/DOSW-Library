package edu.eci.dosw.DOSW_Library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.IBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@DisplayName("Pruebas unitarias de BookController")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book testBook;
    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId("BK_test01");
        testBook.setTitle("El Quijote");
        testBook.setAuthor("Cervantes");
        testBook.setAvailableCopies(3);
        testBook.setTotalCopies(5);

        testBookDTO = new BookDTO();
        testBookDTO.setTitle("El Quijote");
        testBookDTO.setAuthor("Cervantes");
        testBookDTO.setTotalCopies(5);
    }

    // --- POST /api/books ---

    @Test
    @DisplayName("POST /api/books - Agrega libro exitosamente y retorna 201")
    void addBook_success_returns201() throws Exception {
        when(bookService.addBook("El Quijote", "Cervantes", 5)).thenReturn(testBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("BK_test01"))
                .andExpect(jsonPath("$.title").value("El Quijote"))
                .andExpect(jsonPath("$.author").value("Cervantes"))
                .andExpect(jsonPath("$.totalCopies").value(5))
                .andExpect(jsonPath("$.availableCopies").value(3));

        verify(bookService).addBook("El Quijote", "Cervantes", 5);
    }

    @Test
    @DisplayName("POST /api/books - Servicio lanza excepción cuando título es inválido retorna 400")
    void addBook_serviceThrows_returns400() throws Exception {
        when(bookService.addBook(anyString(), anyString(), anyInt()))
                .thenThrow(new IllegalArgumentException("Título no puede ser vacío"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Título no puede ser vacío"));
    }

    // --- GET /api/books ---

    @Test
    @DisplayName("GET /api/books - Retorna lista de libros con 200")
    void getAllBooks_returnsListWith200() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(testBook));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("BK_test01"))
                .andExpect(jsonPath("$[0].title").value("El Quijote"))
                .andExpect(jsonPath("$[0].author").value("Cervantes"));
    }

    @Test
    @DisplayName("GET /api/books - Lista vacía retorna 200 con array vacío")
    void getAllBooks_emptyList_returnsEmptyArray() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/books - Retorna múltiples libros")
    void getAllBooks_multipleBooks_returnsAll() throws Exception {
        Book secondBook = new Book();
        secondBook.setId("BK_test02");
        secondBook.setTitle("Cien años de soledad");
        secondBook.setAuthor("García Márquez");
        secondBook.setAvailableCopies(2);
        secondBook.setTotalCopies(3);

        when(bookService.getAllBooks()).thenReturn(Arrays.asList(testBook, secondBook));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].title").value("Cien años de soledad"));
    }

    // --- GET /api/books/{bookId} ---

    @Test
    @DisplayName("GET /api/books/{bookId} - Retorna libro por ID con 200")
    void getBookById_found_returns200() throws Exception {
        when(bookService.getBookById("BK_test01")).thenReturn(testBook);

        mockMvc.perform(get("/api/books/BK_test01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("BK_test01"))
                .andExpect(jsonPath("$.title").value("El Quijote"))
                .andExpect(jsonPath("$.author").value("Cervantes"));
    }

    @Test
    @DisplayName("GET /api/books/{bookId} - Libro no encontrado retorna 400")
    void getBookById_notFound_returns400() throws Exception {
        when(bookService.getBookById("BK_inexistente"))
                .thenThrow(new IllegalArgumentException("Libro no encontrado"));

        mockMvc.perform(get("/api/books/BK_inexistente"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Libro no encontrado"));
    }

    // --- PUT /api/books/{bookId}/availability ---

    @Test
    @DisplayName("PUT /api/books/{bookId}/availability - Actualiza disponibilidad con 200")
    void updateBookAvailability_success_returns200() throws Exception {
        testBook.setAvailableCopies(2);
        when(bookService.updateBookAvailability("BK_test01", 2)).thenReturn(testBook);

        mockMvc.perform(put("/api/books/BK_test01/availability")
                        .param("availableCopies", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(2));

        verify(bookService).updateBookAvailability("BK_test01", 2);
    }

    @Test
    @DisplayName("PUT /api/books/{bookId}/availability - Copias negativas retorna 400")
    void updateBookAvailability_negativeValue_returns400() throws Exception {
        when(bookService.updateBookAvailability("BK_test01", -1))
                .thenThrow(new IllegalArgumentException("Copias no pueden ser negativas"));

        mockMvc.perform(put("/api/books/BK_test01/availability")
                        .param("availableCopies", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Copias no pueden ser negativas"));
    }
}
