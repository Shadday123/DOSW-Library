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


    @Test
    @DisplayName("getBooksByTitle: debe encontrar libros por coincidencia parcial")
    void getBooksByTitle_partialMatch_returnsBooks() {
        bookService.addBook("El Quijote de la Mancha", "Cervantes", 2);
        bookService.addBook("Cien años de soledad", "García Márquez", 1);

        List<Book> result = bookService.getBooksByTitle("quijote");
        assertEquals(1, result.size());
        assertEquals("El Quijote de la Mancha", result.get(0).getTitle());
    }

    @Test
    @DisplayName("getBooksByTitle: debe ser insensible a mayúsculas")
    void getBooksByTitle_caseInsensitive_returnsBooks() {
        bookService.addBook("El Quijote", "Cervantes", 2);

        List<Book> result = bookService.getBooksByTitle("QUIJOTE");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getBooksByTitle: debe retornar lista vacía si no hay coincidencias")
    void getBooksByTitle_noMatch_returnsEmpty() {
        bookService.addBook("El Quijote", "Cervantes", 2);

        List<Book> result = bookService.getBooksByTitle("Hamlet");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getBooksByAuthor: debe encontrar libros por autor")
    void getBooksByAuthor_found_returnsBooks() {
        bookService.addBook("El Quijote", "Cervantes", 2);
        bookService.addBook("Cien años de soledad", "García Márquez", 1);

        List<Book> result = bookService.getBooksByAuthor("Cervantes");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getBooksByAuthor: debe ser insensible a mayúsculas")
    void getBooksByAuthor_caseInsensitive_returnsBooks() {
        bookService.addBook("El Quijote", "Cervantes", 2);

        List<Book> result = bookService.getBooksByAuthor("cervantes");
        assertEquals(1, result.size());
    }


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


    @Test
    @DisplayName("deleteBook: debe eliminar el libro y retornar true")
    void deleteBook_exists_returnsTrue() {
        Book book = bookService.addBook("El Quijote", "Cervantes", 2);
        boolean deleted = bookService.deleteBook(book.getId());

        assertTrue(deleted);
        assertEquals(0, bookService.getTotalBooks());
    }

    @Test
    @DisplayName("deleteBook: debe retornar false si el libro no existe")
    void deleteBook_notFound_returnsFalse() {
        boolean deleted = bookService.deleteBook("BK_inexistente");
        assertFalse(deleted);
    }


    @Test
    @DisplayName("getTotalBooks: debe contar correctamente los libros")
    void getTotalBooks_returnsCorrectCount() {
        assertEquals(0, bookService.getTotalBooks());

        bookService.addBook("Libro A", "Autor A", 2);
        bookService.addBook("Libro B", "Autor B", 1);

        assertEquals(2, bookService.getTotalBooks());
    }

    @Test
    @DisplayName("getTotalAvailableCopies: debe sumar correctamente todas las copias disponibles")
    void getTotalAvailableCopies_returnsCorrectSum() {
        bookService.addBook("Libro A", "Autor A", 3);
        bookService.addBook("Libro B", "Autor B", 2);

        assertEquals(5, bookService.getTotalAvailableCopies());
    }

    @Test
    @DisplayName("getTotalAvailableCopies: debe actualizarse al prestar un libro")
    void getTotalAvailableCopies_afterLoan_decreasesCorrectly() {
        Book book = bookService.addBook("Libro A", "Autor A", 3);
        bookService.decreaseAvailableCopies(book.getId());

        assertEquals(2, bookService.getTotalAvailableCopies());
    }
}