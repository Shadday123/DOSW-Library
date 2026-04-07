package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import edu.eci.dosw.DOSW_Library.persistence.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de BookService")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        // Mockito handles injection
    }

    // ==================== addBook ====================

    @Test
    @DisplayName("addBook: debe agregar un libro exitosamente")
    void addBook_success() {
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book book = bookService.addBook("El Quijote", "Cervantes", 3);

        assertNotNull(book);
        assertNotNull(book.getId());
        assertTrue(book.getId().startsWith("BK_"));
        assertEquals("El Quijote", book.getTitle());
        assertEquals("Cervantes", book.getAuthor());
        assertEquals(3, book.getTotalCopies());
        assertEquals(3, book.getAvailableCopies());
        verify(bookRepository).save(any(Book.class));
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
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        List<Book> books = bookService.getAllBooks();
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    @DisplayName("getAllBooks: debe retornar todos los libros agregados")
    void getAllBooks_withBooks_returnsAll() {
        List<Book> mockBooks = new ArrayList<>();
        mockBooks.add(new Book("Libro A", "Autor A", "BK_1", 2, 2));
        mockBooks.add(new Book("Libro B", "Autor B", "BK_2", 1, 1));
        when(bookRepository.findAll()).thenReturn(mockBooks);

        List<Book> books = bookService.getAllBooks();
        assertEquals(2, books.size());
    }

    // ==================== getBookById ====================

    @Test
    @DisplayName("getBookById: debe retornar el libro correcto")
    void getBookById_exists_returnsBook() {
        Book mockBook = new Book("El Quijote", "Cervantes", "BK_test01", 3, 3);
        when(bookRepository.findById("BK_test01")).thenReturn(Optional.of(mockBook));

        Book found = bookService.getBookById("BK_test01");

        assertNotNull(found);
        assertEquals("BK_test01", found.getId());
        assertEquals("El Quijote", found.getTitle());
    }

    @Test
    @DisplayName("getBookById: debe lanzar excepción si el libro no existe")
    void getBookById_notFound_throwsException() {
        when(bookRepository.findById("BK_inexistente")).thenReturn(Optional.empty());

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
        Book mockBook = new Book("El Quijote", "Cervantes", "BK_test01", 2, 2);
        when(bookRepository.findById("BK_test01")).thenReturn(Optional.of(mockBook));

        assertTrue(bookService.isBookAvailable("BK_test01"));
    }

    @Test
    @DisplayName("isBookAvailable: debe retornar false cuando no hay copias")
    void isBookAvailable_noCopies_returnsFalse() {
        Book mockBook = new Book("El Quijote", "Cervantes", "BK_test01", 0, 1);
        when(bookRepository.findById("BK_test01")).thenReturn(Optional.of(mockBook));

        assertFalse(bookService.isBookAvailable("BK_test01"));
    }

    // ==================== decreaseAvailableCopies ====================

    @Test
    @DisplayName("decreaseAvailableCopies: debe reducir las copias en uno")
    void decreaseAvailableCopies_success() {
        Book mockBook = new Book("El Quijote", "Cervantes", "BK_test01", 3, 3);
        when(bookRepository.findById("BK_test01")).thenReturn(Optional.of(mockBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookService.decreaseAvailableCopies("BK_test01");

        assertEquals(2, mockBook.getAvailableCopies());
        verify(bookRepository).save(mockBook);
    }

    @Test
    @DisplayName("decreaseAvailableCopies: debe lanzar excepción si no hay copias disponibles")
    void decreaseAvailableCopies_noCopies_throwsBookNotAvailableException() {
        Book mockBook = new Book("El Quijote", "Cervantes", "BK_test01", 0, 1);
        when(bookRepository.findById("BK_test01")).thenReturn(Optional.of(mockBook));

        assertThrows(BookNotAvailableException.class,
                () -> bookService.decreaseAvailableCopies("BK_test01"));
    }

    // ==================== increaseAvailableCopies ====================

    @Test
    @DisplayName("increaseAvailableCopies: debe aumentar las copias en uno")
    void increaseAvailableCopies_success() {
        Book mockBook = new Book("El Quijote", "Cervantes", "BK_test01", 2, 3);
        when(bookRepository.findById("BK_test01")).thenReturn(Optional.of(mockBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookService.increaseAvailableCopies("BK_test01");

        assertEquals(3, mockBook.getAvailableCopies());
        verify(bookRepository).save(mockBook);
    }

    @Test
    @DisplayName("increaseAvailableCopies: debe lanzar excepción si ya está en el máximo")
    void increaseAvailableCopies_atMax_throwsException() {
        Book mockBook = new Book("El Quijote", "Cervantes", "BK_test01", 2, 2);
        when(bookRepository.findById("BK_test01")).thenReturn(Optional.of(mockBook));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.increaseAvailableCopies("BK_test01"));
    }

    // ==================== updateBookAvailability ====================

    @Test
    @DisplayName("updateBookAvailability: debe actualizar las copias disponibles")
    void updateBookAvailability_success() {
        Book mockBook = new Book("El Quijote", "Cervantes", "BK_test01", 5, 5);
        when(bookRepository.findById("BK_test01")).thenReturn(Optional.of(mockBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book updated = bookService.updateBookAvailability("BK_test01", 3);

        assertEquals(3, updated.getAvailableCopies());
    }

    @Test
    @DisplayName("updateBookAvailability: debe lanzar excepción si supera el total")
    void updateBookAvailability_exceedsTotal_throwsException() {
        Book mockBook = new Book("El Quijote", "Cervantes", "BK_test01", 3, 3);
        when(bookRepository.findById("BK_test01")).thenReturn(Optional.of(mockBook));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBookAvailability("BK_test01", 5));
    }

    @Test
    @DisplayName("updateBookAvailability: debe lanzar excepción si es negativo")
    void updateBookAvailability_negative_throwsException() {
        Book mockBook = new Book("El Quijote", "Cervantes", "BK_test01", 3, 3);
        when(bookRepository.findById("BK_test01")).thenReturn(Optional.of(mockBook));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBookAvailability("BK_test01", -1));
    }
}
