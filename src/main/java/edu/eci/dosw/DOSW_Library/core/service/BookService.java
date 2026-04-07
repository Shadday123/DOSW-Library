package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import edu.eci.dosw.DOSW_Library.persistence.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService implements IBookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(String title, String author, int totalCopies) {
        ValidationUtil.validateNotEmpty(title, "Título del libro");
        ValidationUtil.validateNotEmpty(author, "Autor del libro");

        if (totalCopies <= 0) {
            throw new IllegalArgumentException("El número de copias debe ser mayor a cero");
        }

        Book book = new Book();
        book.setId(IdGeneratorUtil.generateBookId());
        book.setTitle(title);
        book.setAuthor(author);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(totalCopies);

        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(String bookId) {
        ValidationUtil.validateNotEmpty(bookId, "ID del libro");

        return bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con ID: " + bookId));
    }

    public boolean isBookAvailable(String bookId) {
        Book book = getBookById(bookId);
        return book.getAvailableCopies() > 0;
    }

    public void decreaseAvailableCopies(String bookId) {
        Book book = getBookById(bookId);

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException(bookId, book.getTitle());
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }

    public void increaseAvailableCopies(String bookId) {
        Book book = getBookById(bookId);

        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new IllegalArgumentException(
                    "No se pueden aumentar más copias: el libro '" + book.getTitle() + "' ya está en el máximo");
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }

    public Book updateBookAvailability(String bookId, int newAvailableCopies) {
        Book book = getBookById(bookId);
        ValidationUtil.validateCopies(newAvailableCopies, book.getTotalCopies());

        book.setAvailableCopies(newAvailableCopies);
        return bookRepository.save(book);
    }
}
