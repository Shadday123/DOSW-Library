package edu.eci.dosw.DOSW_Library.dto;

import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de BookDTO")
class BookDTOTest {

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
        testBookDTO.setId("BK_test01");
        testBookDTO.setTitle("El Quijote");
        testBookDTO.setAuthor("Cervantes");
        testBookDTO.setAvailableCopies(3);
        testBookDTO.setTotalCopies(5);
    }

    // --- modelToDTO ---

    @Test
    @DisplayName("modelToDTO - Convierte Book a BookDTO correctamente")
    void modelToDTO_validBook_returnsCorrectDTO() {
        BookDTO result = BookDTO.modelToDTO(testBook);

        assertNotNull(result);
        assertEquals("BK_test01", result.getId());
        assertEquals("El Quijote", result.getTitle());
        assertEquals("Cervantes", result.getAuthor());
        assertEquals(3, result.getAvailableCopies());
        assertEquals(5, result.getTotalCopies());
    }

    @Test
    @DisplayName("modelToDTO - Book nulo retorna null")
    void modelToDTO_nullBook_returnsNull() {
        BookDTO result = BookDTO.modelToDTO(null);

        assertNull(result);
    }

    @Test
    @DisplayName("modelToDTO - Campos opcionales nulos se copian como null")
    void modelToDTO_bookWithNullFields_copiesNullFields() {
        Book bookWithNulls = new Book();
        bookWithNulls.setId(null);
        bookWithNulls.setTitle(null);
        bookWithNulls.setAuthor(null);
        bookWithNulls.setAvailableCopies(0);
        bookWithNulls.setTotalCopies(0);

        BookDTO result = BookDTO.modelToDTO(bookWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getTitle());
        assertNull(result.getAuthor());
        assertEquals(0, result.getAvailableCopies());
        assertEquals(0, result.getTotalCopies());
    }

    @Test
    @DisplayName("modelToDTO - Preserva disponibilidad cero")
    void modelToDTO_zeroCopies_preservesZero() {
        testBook.setAvailableCopies(0);

        BookDTO result = BookDTO.modelToDTO(testBook);

        assertEquals(0, result.getAvailableCopies());
    }

    // --- dtoToModel ---

    @Test
    @DisplayName("dtoToModel - Convierte BookDTO a Book correctamente")
    void dtoToModel_validDTO_returnsCorrectBook() {
        Book result = BookDTO.dtoToModel(testBookDTO);

        assertNotNull(result);
        assertEquals("BK_test01", result.getId());
        assertEquals("El Quijote", result.getTitle());
        assertEquals("Cervantes", result.getAuthor());
        assertEquals(3, result.getAvailableCopies());
        assertEquals(5, result.getTotalCopies());
    }

    @Test
    @DisplayName("dtoToModel - BookDTO nulo retorna null")
    void dtoToModel_nullDTO_returnsNull() {
        Book result = BookDTO.dtoToModel(null);

        assertNull(result);
    }

    @Test
    @DisplayName("dtoToModel - Campos nulos en DTO se copian como null")
    void dtoToModel_dtoWithNullFields_copiesNullFields() {
        BookDTO dtoWithNulls = new BookDTO();
        dtoWithNulls.setId(null);
        dtoWithNulls.setTitle(null);
        dtoWithNulls.setAuthor(null);

        Book result = BookDTO.dtoToModel(dtoWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getTitle());
        assertNull(result.getAuthor());
    }

    // --- Conversión de ida y vuelta ---

    @Test
    @DisplayName("modelToDTO -> dtoToModel - Conversión de ida y vuelta preserva datos")
    void roundTrip_modelToDTOThenBack_preservesData() {
        BookDTO dto = BookDTO.modelToDTO(testBook);
        Book result = BookDTO.dtoToModel(dto);

        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        assertEquals(testBook.getAuthor(), result.getAuthor());
        assertEquals(testBook.getAvailableCopies(), result.getAvailableCopies());
        assertEquals(testBook.getTotalCopies(), result.getTotalCopies());
    }

    @Test
    @DisplayName("dtoToModel -> modelToDTO - Conversión inversa preserva datos")
    void roundTrip_dtoToModelThenBack_preservesData() {
        Book book = BookDTO.dtoToModel(testBookDTO);
        BookDTO result = BookDTO.modelToDTO(book);

        assertEquals(testBookDTO.getId(), result.getId());
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        assertEquals(testBookDTO.getAuthor(), result.getAuthor());
        assertEquals(testBookDTO.getAvailableCopies(), result.getAvailableCopies());
        assertEquals(testBookDTO.getTotalCopies(), result.getTotalCopies());
    }

    // --- Constructor y getters/setters (Lombok @Data) ---

    @Test
    @DisplayName("Constructor sin argumentos crea objeto vacío")
    void noArgsConstructor_createsEmptyObject() {
        BookDTO dto = new BookDTO();

        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getAuthor());
        assertEquals(0, dto.getAvailableCopies());
        assertEquals(0, dto.getTotalCopies());
    }

    @Test
    @DisplayName("Constructor con todos los argumentos inicializa correctamente")
    void allArgsConstructor_initializesCorrectly() {
        BookDTO dto = new BookDTO("El Quijote", "Cervantes", "BK_001", 3, 5);

        assertEquals("El Quijote", dto.getTitle());
        assertEquals("Cervantes", dto.getAuthor());
        assertEquals("BK_001", dto.getId());
        assertEquals(3, dto.getAvailableCopies());
        assertEquals(5, dto.getTotalCopies());
    }

    @Test
    @DisplayName("Setters actualizan los campos correctamente")
    void setters_updateFieldsCorrectly() {
        BookDTO dto = new BookDTO();
        dto.setTitle("Nuevo Título");
        dto.setAuthor("Nuevo Autor");
        dto.setId("BK_nuevo");
        dto.setAvailableCopies(2);
        dto.setTotalCopies(4);

        assertEquals("Nuevo Título", dto.getTitle());
        assertEquals("Nuevo Autor", dto.getAuthor());
        assertEquals("BK_nuevo", dto.getId());
        assertEquals(2, dto.getAvailableCopies());
        assertEquals(4, dto.getTotalCopies());
    }
}
