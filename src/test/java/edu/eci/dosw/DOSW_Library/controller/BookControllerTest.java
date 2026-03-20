package edu.eci.dosw.DOSW_Library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private BookService bookService;

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
    @DisplayName("POST /api/books - Servicio lanza excepción cuando título es inválido")
    void addBook_serviceThrows_propagatesException() {
        when(bookService.addBook(anyString(), anyString(), anyInt()))
                .thenThrow(new IllegalArgumentException("Título no puede ser vacío"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO))));
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
    @DisplayName("GET /api/books/{bookId} - Libro no encontrado propaga excepción")
    void getBookById_notFound_propagatesException() {
        when(bookService.getBookById("BK_inexistente"))
                .thenThrow(new IllegalArgumentException("Libro no encontrado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/books/BK_inexistente")));
    }

    // --- GET /api/books/{bookId}/available ---

    @Test
    @DisplayName("GET /api/books/{bookId}/available - Retorna true si disponible")
    void isBookAvailable_available_returnsTrue() throws Exception {
        when(bookService.isBookAvailable("BK_test01")).thenReturn(true);

        mockMvc.perform(get("/api/books/BK_test01/available"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/books/{bookId}/available - Retorna false si no disponible")
    void isBookAvailable_notAvailable_returnsFalse() throws Exception {
        when(bookService.isBookAvailable("BK_test01")).thenReturn(false);

        mockMvc.perform(get("/api/books/BK_test01/available"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
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
    @DisplayName("PUT /api/books/{bookId}/availability - Copias negativas propaga excepción")
    void updateBookAvailability_negativeValue_propagatesException() {
        when(bookService.updateBookAvailability("BK_test01", -1))
                .thenThrow(new IllegalArgumentException("Copias no pueden ser negativas"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(put("/api/books/BK_test01/availability")
                        .param("availableCopies", "-1")));
    }

    // --- DELETE /api/books/{bookId} ---

    @Test
    @DisplayName("DELETE /api/books/{bookId} - Elimina libro con 204")
    void deleteBook_success_returns204() throws Exception {
        when(bookService.deleteBook("BK_test01")).thenReturn(true);

        mockMvc.perform(delete("/api/books/BK_test01"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook("BK_test01");
    }

    @Test
    @DisplayName("DELETE /api/books/{bookId} - Libro no encontrado propaga excepción")
    void deleteBook_notFound_propagatesException() {
        when(bookService.deleteBook("BK_inexistente"))
                .thenThrow(new IllegalArgumentException("Libro no encontrado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(delete("/api/books/BK_inexistente")));
    }

    // --- GET /api/books/search/title ---

    @Test
    @DisplayName("GET /api/books/search/title - Busca por título parcial y retorna lista")
    void getBooksByTitle_found_returnsList() throws Exception {
        when(bookService.getBooksByTitle("Quijote")).thenReturn(Arrays.asList(testBook));

        mockMvc.perform(get("/api/books/search/title")
                        .param("title", "Quijote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("El Quijote"));
    }

    @Test
    @DisplayName("GET /api/books/search/title - Sin coincidencias retorna lista vacía")
    void getBooksByTitle_noMatch_returnsEmpty() throws Exception {
        when(bookService.getBooksByTitle("XYZ")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books/search/title")
                        .param("title", "XYZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/books/search/author ---

    @Test
    @DisplayName("GET /api/books/search/author - Busca por autor y retorna lista")
    void getBooksByAuthor_found_returnsList() throws Exception {
        when(bookService.getBooksByAuthor("Cervantes")).thenReturn(Arrays.asList(testBook));

        mockMvc.perform(get("/api/books/search/author")
                        .param("author", "Cervantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].author").value("Cervantes"));
    }

    @Test
    @DisplayName("GET /api/books/search/author - Sin coincidencias retorna lista vacía")
    void getBooksByAuthor_noMatch_returnsEmpty() throws Exception {
        when(bookService.getBooksByAuthor("Desconocido")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books/search/author")
                        .param("author", "Desconocido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/books/stats/total ---

    @Test
    @DisplayName("GET /api/books/stats/total - Retorna total de libros únicos")
    void getTotalBooks_returnsCount() throws Exception {
        when(bookService.getTotalBooks()).thenReturn(5);

        mockMvc.perform(get("/api/books/stats/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @DisplayName("GET /api/books/stats/total - Sin libros retorna 0")
    void getTotalBooks_noBooks_returnsZero() throws Exception {
        when(bookService.getTotalBooks()).thenReturn(0);

        mockMvc.perform(get("/api/books/stats/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    // --- GET /api/books/stats/available ---

    @Test
    @DisplayName("GET /api/books/stats/available - Retorna total de copias disponibles")
    void getTotalAvailableCopies_returnsCount() throws Exception {
        when(bookService.getTotalAvailableCopies()).thenReturn(12);

        mockMvc.perform(get("/api/books/stats/available"))
                .andExpect(status().isOk())
                .andExpect(content().string("12"));
    }

    @Test
    @DisplayName("GET /api/books/stats/available - Sin copias retorna 0")
    void getTotalAvailableCopies_noCopies_returnsZero() throws Exception {
        when(bookService.getTotalAvailableCopies()).thenReturn(0);

        mockMvc.perform(get("/api/books/stats/available"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
