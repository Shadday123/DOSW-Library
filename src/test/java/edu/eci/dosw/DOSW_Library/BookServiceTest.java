package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de BookService")
class BookServiceTest {

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
    }

    // ==================== addBook ====================

    @Test
    @DisplayName("addBook: debe agregar un libro exitosamente")
    void addBook_success() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 3);

        assertNotNull(book);
        assertNotNull(book.getId());
        assertTrue(book.getId().startsWith("BK_"));
        assertEquals("El Quijote", book.getTitle());
        assertEquals("Cervantes", book.getAuthor());
        assertEquals(3, book.getTotalCopies());
        assertEquals(3, book.getAvailableCopies());
    }

    @Test
    @DisplayName("addBook: debe lanzar excepción si el título está vacío")
    void addBook_emptyTitle_throwsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.addBook("", "Cervantes", 3)
        );
        assertTrue(ex.getMessage().contains("Título del libro"));
    }

    @Test
    @DisplayName("addBook: debe lanzar excepción si el título es nulo")
    void addBook_nullTitle_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(null, "Cervantes", 3));
    }

    @Test
    @DisplayName("addBook: debe lanzar excepción si el autor está vacío")
    void addBook_emptyAuthor_throwsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.addBook("El Quijote", "", 3)
        );
        assertTrue(ex.getMessage().contains("Autor del libro"));
    }

    @Test
    @DisplayName("addBook: debe lanzar excepción si el número de copias es cero")
    void addBook_zeroCopies_throwsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.addBook("El Quijote", "Cervantes", 0)
        );
        assertTrue(ex.getMessage().contains("mayor a cero"));
    }

    @Test
    @DisplayName("addBook: debe lanzar excepción si el número de copias es negativo")
    void addBook_negativeCopies_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook("El Quijote", "Cervantes", -1));
    }

    // ==================== getAllBooks ====================

    @Test
    @DisplayName("getAllBooks: debe retornar lista vacía cuando no hay libros")
    void getAllBooks_empty_returnsEmptyList() {
        List<Book> books = bookService.getAllBooks();
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    @DisplayName("getAllBooks: debe retornar todos los libros agregados")
    void getAllBooks_withBooks_returnsAll() {
        bookService.addBook("Libro A", "Autor A", 2);
        bookService.addBook("Libro B", "Autor B", 1);

        List<Book> books = bookService.getAllBooks();
        assertEquals(2, books.size());
    }

    // ==================== getBookById ====================

    @Test
    @DisplayName("getBookById: debe retornar el libro correcto")
    void getBookById_exists_returnsBook() {
        Book added = bookService.addBook("El Quijote", "Cervantes", 3);
        Book found = bookService.getBookById(added.getId());

        assertNotNull(found);
        assertEquals(added.getId(), found.getId());
        assertEquals("El Quijote", found.getTitle());
    }

    @Test
    @DisplayName("getBookById: debe lanzar excepción si el libro no existe")
    void getBookById_notFound_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.getBookById("BK_inexistente"));
    }

    @Test
    @DisplayName("getBookById: debe lanzar excepción si el ID es vacío")
    void getBookById_emptyId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.getBookById(""));
    }

    // ==================== isBookAvailable ====================

    @Test
    @DisplayName("isBookAvailable: debe retornar true cuando hay copias")
    void isBookAvailable_withCopies_returnsTrue() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 2);
        assertTrue(bookService.isBookAvailable(book.getId()));
    }

    @Test
    @DisplayName("isBookAvailable: debe retornar false cuando no hay copias")
    void isBookAvailable_noCopies_returnsFalse() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 1);
        bookService.decreaseAvailableCopies(book.getId());

        assertFalse(bookService.isBookAvailable(book.getId()));
    }

    // ==================== decreaseAvailableCopies ====================

    @Test
    @DisplayName("decreaseAvailableCopies: debe reducir las copias en uno")
    void decreaseAvailableCopies_success() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 3);
        bookService.decreaseAvailableCopies(book.getId());

        assertEquals(2, bookService.getBookById(book.getId()).getAvailableCopies());
    }

    @Test
    @DisplayName("decreaseAvailableCopies: debe lanzar excepción si no hay copias disponibles")
    void decreaseAvailableCopies_noCopies_throwsBookNotAvailableException() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 1);
        bookService.decreaseAvailableCopies(book.getId());

        assertThrows(BookNotAvailableException.class,
                () -> bookService.decreaseAvailableCopies(book.getId()));
    }

    // ==================== increaseAvailableCopies ====================

    @Test
    @DisplayName("increaseAvailableCopies: debe aumentar las copias en uno")
    void increaseAvailableCopies_success() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 3);
        bookService.decreaseAvailableCopies(book.getId());
        bookService.increaseAvailableCopies(book.getId());

        assertEquals(3, bookService.getBookById(book.getId()).getAvailableCopies());
    }

    @Test
    @DisplayName("increaseAvailableCopies: debe lanzar excepción si ya está en el máximo")
    void increaseAvailableCopies_atMax_throwsException() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 2);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.increaseAvailableCopies(book.getId()));
    }

    // ==================== updateBookAvailability ====================

    @Test
    @DisplayName("updateBookAvailability: debe actualizar las copias disponibles")
    void updateBookAvailability_success() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 5);
        Book updated = bookService.updateBookAvailability(book.getId(), 3);

        assertEquals(3, updated.getAvailableCopies());
    }

    @Test
    @DisplayName("updateBookAvailability: debe lanzar excepción si supera el total")
    void updateBookAvailability_exceedsTotal_throwsException() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 3);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBookAvailability(book.getId(), 5));
    }

    @Test
    @DisplayName("updateBookAvailability: debe lanzar excepción si es negativo")
    void updateBookAvailability_negative_throwsException() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 3);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBookAvailability(book.getId(), -1));
    }
}
