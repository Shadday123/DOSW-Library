package edu.eci.dosw.DOSW_Library.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.controller.mapper.BookMapper;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de BookMapper")
class BookMapperTest {

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
    @DisplayName("modelToDTO - Convierte Book a BookDTO con todos los campos")
    void modelToDTO_validBook_mapsAllFields() {
        BookDTO result = BookMapper.modelToDTO(testBook);

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
        BookDTO result = BookMapper.modelToDTO(null);

        assertNull(result);
    }

    @Test
    @DisplayName("modelToDTO - Mapea libro con cero copias disponibles")
    void modelToDTO_zeroCopies_preservesZero() {
        testBook.setAvailableCopies(0);

        BookDTO result = BookMapper.modelToDTO(testBook);

        assertEquals(0, result.getAvailableCopies());
        assertEquals(5, result.getTotalCopies());
    }

    @Test
    @DisplayName("modelToDTO - Mapea libro con campos nulos")
    void modelToDTO_nullFields_mapsAsNull() {
        Book bookWithNulls = new Book();
        bookWithNulls.setId(null);
        bookWithNulls.setTitle(null);
        bookWithNulls.setAuthor(null);

        BookDTO result = BookMapper.modelToDTO(bookWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getTitle());
        assertNull(result.getAuthor());
    }

    @Test
    @DisplayName("modelToDTO - Crea una nueva instancia de BookDTO (no modifica el original)")
    void modelToDTO_returnsNewInstance() {
        BookDTO result1 = BookMapper.modelToDTO(testBook);
        BookDTO result2 = BookMapper.modelToDTO(testBook);

        assertNotSame(result1, result2);
    }

    // --- dtoToModel ---

    @Test
    @DisplayName("dtoToModel - Convierte BookDTO a Book con todos los campos")
    void dtoToModel_validDTO_mapsAllFields() {
        Book result = BookMapper.dtoToModel(testBookDTO);

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
        Book result = BookMapper.dtoToModel(null);

        assertNull(result);
    }

    @Test
    @DisplayName("dtoToModel - Mapea DTO con campos nulos")
    void dtoToModel_nullFields_mapsAsNull() {
        BookDTO dtoWithNulls = new BookDTO();
        dtoWithNulls.setId(null);
        dtoWithNulls.setTitle(null);
        dtoWithNulls.setAuthor(null);

        Book result = BookMapper.dtoToModel(dtoWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getTitle());
        assertNull(result.getAuthor());
    }

    @Test
    @DisplayName("dtoToModel - Crea una nueva instancia de Book (no modifica el original)")
    void dtoToModel_returnsNewInstance() {
        Book result1 = BookMapper.dtoToModel(testBookDTO);
        Book result2 = BookMapper.dtoToModel(testBookDTO);

        assertNotSame(result1, result2);
    }

    // --- Conversión de ida y vuelta ---

    @Test
    @DisplayName("modelToDTO -> dtoToModel - Ida y vuelta preserva todos los datos")
    void roundTrip_modelToDTOAndBack_preservesAllData() {
        BookDTO dto = BookMapper.modelToDTO(testBook);
        Book result = BookMapper.dtoToModel(dto);

        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        assertEquals(testBook.getAuthor(), result.getAuthor());
        assertEquals(testBook.getAvailableCopies(), result.getAvailableCopies());
        assertEquals(testBook.getTotalCopies(), result.getTotalCopies());
    }

    @Test
    @DisplayName("dtoToModel -> modelToDTO - Vuelta y ida preserva todos los datos")
    void roundTrip_dtoToModelAndBack_preservesAllData() {
        Book book = BookMapper.dtoToModel(testBookDTO);
        BookDTO result = BookMapper.modelToDTO(book);

        assertEquals(testBookDTO.getId(), result.getId());
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        assertEquals(testBookDTO.getAuthor(), result.getAuthor());
        assertEquals(testBookDTO.getAvailableCopies(), result.getAvailableCopies());
        assertEquals(testBookDTO.getTotalCopies(), result.getTotalCopies());
    }

    @Test
    @DisplayName("modelToDTO - Copias disponibles iguales al total se mapean correctamente")
    void modelToDTO_availableEqualTotal_mapsCorrectly() {
        testBook.setAvailableCopies(5);
        testBook.setTotalCopies(5);

        BookDTO result = BookMapper.modelToDTO(testBook);

        assertEquals(5, result.getAvailableCopies());
        assertEquals(5, result.getTotalCopies());
    }
}
